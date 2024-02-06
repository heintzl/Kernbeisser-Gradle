package kernbeisser.Security.Bytecode;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Useful.Tools;
import lombok.SneakyThrows;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/** faster imp of the SubMethodVisitor by buffering target methods */
public class PermissionKeyMethodVisitor extends ClassVisitor {
  private static final HashMap<AnalyserTarget, PermissionSet> cache = new HashMap<>();

  private int depth;
  private String targetMethodName;
  private String targetDescriptor;
  private final Set<String> visited;
  private final PermissionSet collected;

  public PermissionKeyMethodVisitor(int depth, AnalyserTarget target, PermissionSet collector) {
    super(Opcodes.ASM7);
    this.depth = depth;
    targetMethodName = target.getTargetMethodName();
    targetDescriptor = target.getTargetDescriptor();
    visited = new HashSet<>();
    visited.add(
        target.getTargetClass().getName().replace(".", "/") + targetMethodName + targetDescriptor);
    collected = collector;
  }

  @Override
  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {
    if (depth == 0
        || !targetMethodName.equals(name)
        || !Objects.equals(descriptor, targetDescriptor)) return null;
    return new MethodVisitor(Opcodes.ASM7) {
      @Override
      public void visitMethodInsn(
          int opcode, String owner, String name, String descriptor, boolean isInterface) {
        try {
          if (owner.startsWith("[L")) {
            return;
          }
          if (visited.add(owner + name + descriptor)) {
            if (!owner.startsWith("kernbeisser")) return;
            ClassReader cr = new ClassReader(owner);
            String targetNameBuffer = targetMethodName;
            String targetDescriptionBuffer = targetDescriptor;
            int bufferDepth = depth;
            targetMethodName = name;
            targetDescriptor = descriptor;
            depth--;
            cr.accept(PermissionKeyMethodVisitor.this, 0);
            depth = bufferDepth;
            targetMethodName = targetNameBuffer;
            targetDescriptor = targetDescriptionBuffer;
          }
        } catch (Exception e) {
          Tools.showUnexpectedErrorWarning(e);
        }
      }

      @Override
      public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return new AnnotationVisitor(Opcodes.ASM7) {
          @Override
          public void visitEnum(String name, String descriptor, String value) {
            if (descriptor.replace("/", ".").endsWith(PermissionKey.class.getName() + ";")) {
              collected.addPermission(PermissionKey.valueOf(value));
            }
          }

          @Override
          public AnnotationVisitor visitArray(String name) {
            return this;
          }
        };
      }
    };
  }

  @SneakyThrows
  public static PermissionSet accessedKeys(Serializable serializable) {
    return accessedKeys(serializable, new PermissionSet(), -1);
  }

  @SneakyThrows
  // searches the binary class for methods look-ups which requires permission
  public static PermissionSet accessedKeys(
      Serializable serializable, PermissionSet collector, int depth) {
    return peekTargets(AnalyserTarget.ofLambda(serializable), collector, depth);
  }

  @SneakyThrows
  public static PermissionSet peekTargets(
      AnalyserTarget[] analyserTargets, PermissionSet collector, int depth) {
    for (AnalyserTarget analyserTarget : analyserTargets) {
      peekTarget(analyserTarget, collector, depth);
    }
    return collector;
  }

  public static void peekTarget(AnalyserTarget method, PermissionSet collector, int depth)
      throws IOException {
    PermissionSet cached = cache.get(method);
    if (cached != null) {
      collector.addAll(cached);
    }
    PermissionKeyMethodVisitor subMethodVisitor =
        new PermissionKeyMethodVisitor(depth, method, collector);
    ClassReader cr = new ClassReader(method.getTargetClass().getName());
    cr.accept(subMethodVisitor, 0);
    cache.put(method, subMethodVisitor.collected);
  }
}
