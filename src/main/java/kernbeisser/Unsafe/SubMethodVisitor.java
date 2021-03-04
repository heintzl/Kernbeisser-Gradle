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
import kernbeisser.Useful.Tools;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SubMethodVisitor extends ClassVisitor {

  private final int depth;
  private final String targetMethodName;
  private final String targetDescriptor;
  private final Set<Method> visted;

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

  public SubMethodVisitor(int depth, String target, String signature, Set<Method> alreadyVisited)
      throws Exception {
    super(Opcodes.ASM7);
    this.depth = depth;
    this.targetMethodName = target;
    this.targetDescriptor = signature;
    this.visted = alreadyVisited;
  }

  public SubMethodVisitor(int depth, Serializable serializable) throws Exception {
    this(depth, getReflectedMethod(serializable));
  }

  public SubMethodVisitor(int depth, Method method) {
    super(Opcodes.ASM7);
    this.depth = depth;
    targetMethodName = method.getName();
    targetDescriptor = getSignature(method);
    visted = new HashSet<>();
    visted.add(method);
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
          Method m =
              getByNameAndDescriptor(Class.forName(owner.replace("/", ".")), name, descriptor);
          if (m == null || visted.add(m)) {

            ClassReader cr = new ClassReader(owner);
            cr.accept(new SubMethodVisitor(depth - 1, name, descriptor, visted), 0);
          }
        } catch (Exception e) {
          Tools.showUnexpectedErrorWarning(e);
        }
      }
    };
  }

  @SneakyThrows
  public static Set<Method> accessedMethods(Serializable serializable) throws IOException {
    Method method = getReflectedMethod(serializable);
    SubMethodVisitor subMethodVisitor = new SubMethodVisitor(4, method);
    ClassReader cr = new ClassReader(method.getDeclaringClass().getName());
    cr.accept(subMethodVisitor, 0);
    return subMethodVisitor.visted;
  }
}
