package kernbeisser.Unsafe;

import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

  private int depth;
  private String targetMethodName;
  private String targetDescriptor;
  private final Set<String> visited;
  private final PermissionSet collected = new PermissionSet();

  @SneakyThrows
  private static Method getByNameAndDescriptor(
      Class<?> cl, String targetMethodName, String targetDescriptor) {

    Field gSig = Method.class.getDeclaredField("signature");
    gSig.setAccessible(true);
    if (targetMethodName.equals("<init>")) return null;
    do {
      for (Method declaredMethod : cl.getDeclaredMethods()) {
        if (declaredMethod.getName().equals(targetMethodName)
            && targetDescriptor.equals(getSignature(declaredMethod, gSig))) {
          return declaredMethod;
        }
      }
      cl = cl.getSuperclass();
    } while (!cl.equals(Object.class));
    throw new UnsupportedOperationException("cannot find method");
  }

  private static Method getReflectedMethod(Serializable lambda) throws Exception {
    Method m = lambda.getClass().getDeclaredMethod("writeReplace");
    m.setAccessible(true);
    SerializedLambda sl = (SerializedLambda) m.invoke(lambda);
    Class<?> cl = Class.forName(sl.getImplClass().replace("/", "."));
    Field gSig = Method.class.getDeclaredField("signature");
    gSig.setAccessible(true);
    for (Method declaredMethod : cl.getDeclaredMethods()) {
      if (declaredMethod.getName().equals(sl.getImplMethodName())
          && sl.getImplMethodSignature().equals(getSignature(declaredMethod, gSig))) {
        return declaredMethod;
      }
    }
    throw new UnsupportedOperationException("cannot find method");
  }

  public static String getSignature(Method m, Field signature) {
    String sig;
    try {
      signature.setAccessible(true);
      sig = (String) signature.get(m);
      if (sig != null) return sig;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    StringBuilder sb = new StringBuilder("(");
    for (Class<?> c : m.getParameterTypes())
      sb.append((sig = Array.newInstance(c, 0).toString()), 1, sig.indexOf('@'));
    return sb.append(')')
        .append(
            m.getReturnType() == void.class
                ? "V"
                : (sig = Array.newInstance(m.getReturnType(), 0).toString())
                    .substring(1, sig.indexOf('@')))
        .toString()
        .replace(".", "/");
  }

  @SneakyThrows
  public static String getSignature(Method m) {
    Field sig = Method.class.getDeclaredField("signature");
    sig.setAccessible(true);
    return getSignature(m, sig);
  }

  public PermissionKeyMethodVisitor(
      int depth, String target, String signature, Set<String> alreadyVisited) throws Exception {
    super(Opcodes.ASM7);
    this.depth = depth;
    this.targetMethodName = target;
    this.targetDescriptor = signature;
    this.visited = alreadyVisited;
  }

  public PermissionKeyMethodVisitor(int depth, Serializable serializable) throws Exception {
    this(depth, getReflectedMethod(serializable));
  }

  public PermissionKeyMethodVisitor(int depth, Method method) {
    super(Opcodes.ASM7);
    this.depth = depth;
    targetMethodName = method.getName();
    targetDescriptor = getSignature(method);
    visited = new HashSet<>();
    visited.add(
        method.getDeclaringClass().getName().replace(".", "/")
            + targetMethodName
            + targetDescriptor);
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
  public static PermissionSet accessedKeys(Serializable serializable) throws IOException {
    Method method = getReflectedMethod(serializable);
    PermissionKeyMethodVisitor subMethodVisitor = new PermissionKeyMethodVisitor(4, method);
    ClassReader cr = new ClassReader(method.getDeclaringClass().getName());
    cr.accept(subMethodVisitor, 0);
    return subMethodVisitor.collected;
  }
}
