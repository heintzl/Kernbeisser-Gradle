package kernbeisser.Security.Bytecode;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.Data;
import lombok.SneakyThrows;

@Data
public class AnalyserTarget {

  private final String targetMethodName, targetDescriptor;
  private final Class<?> targetClass;

  public AnalyserTarget(String targetMethodName, String targetDescriptor, Class<?> targetClass) {
    this.targetMethodName = targetMethodName;
    this.targetDescriptor = targetDescriptor;
    this.targetClass = targetClass;
  }

  @SneakyThrows
  public static AnalyserTarget[] ofLambda(Serializable serializable) {
    try {
      return new AnalyserTarget[] {getReflectedMethod(serializable)};
    } catch (UnsupportedOperationException e) {
      Method[] methods = serializable.getClass().getDeclaredMethods();
      AnalyserTarget[] analyserTargets = new AnalyserTarget[methods.length];
      for (int i = 0; i < methods.length; i++) {
        analyserTargets[i] = AnalyserTarget.ofMethod(methods[i]);
      }
      return analyserTargets;
    }
  }

  public static AnalyserTarget ofMethod(Method method) {
    return new AnalyserTarget(method.getName(), getSignature(method), method.getDeclaringClass());
  }

  private static AnalyserTarget getReflectedMethod(Serializable lambda) throws Exception {
    try {
      Method m = lambda.getClass().getDeclaredMethod("writeReplace");
      m.setAccessible(true);
      SerializedLambda sl = (SerializedLambda) m.invoke(lambda);
      Class<?> cl = Class.forName(sl.getImplClass().replace("/", "."));
      return new AnalyserTarget(sl.getImplMethodName(), sl.getImplMethodSignature(), cl);
    } catch (NoSuchMethodException | ClassCastException e) {
      throw new UnsupportedOperationException("not a lambda instance");
    }
  }

  @SneakyThrows
  public static String getSignature(Method m) {
    Field sig = Method.class.getDeclaredField("signature");
    sig.setAccessible(true);
    return getSignature(m, sig);
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

  // maps most of the functions
  // otherwise you can still create just a serialized lambda with the methods
  public interface FunctionR<R> extends Serializable {
    R function() throws Throwable;
  }

  public interface FunctionRT<R, T> extends Serializable {
    R function(T t) throws Throwable;
  }

  public interface FunctionRTT<R, T, T1> extends Serializable {
    R function(T t, T1 t1) throws Throwable;
  }

  public interface FunctionRTTT<R, T, T1, T2> extends Serializable {
    R function(T t, T1 t1, T2 t2) throws Throwable;
  }

  public interface FunctionRTTTT<R, T, T1, T2, T3> extends Serializable {
    R function(T t, T1 t1, T2 t2, T3 t3) throws Throwable;
  }

  public interface FunctionRTTTTT<R, T, T1, T2, T3, T4> extends Serializable {
    R function(T t, T1 t1, T2 t2, T3 t3, T4 t4) throws Throwable;
  }

  public interface FunctionT<T> extends Serializable {
    void function(T t) throws Throwable;
  }

  public interface FunctionTT<T, T1> extends Serializable {
    void function(T t, T1 t1) throws Throwable;
  }

  public interface FunctionTTT<T, T1, T2> extends Serializable {
    void function(T t, T1 t1, T2 t2) throws Throwable;
  }

  public interface FunctionTTTT<T, T1, T2, T3> extends Serializable {
    void function(T t, T1 t1, T2 t2, T3 t3) throws Throwable;
  }

  public interface FunctionTTTTT<T, T1, T2, T3, T4> extends Serializable {
    void function(T t, T1 t1, T2 t2, T3 t3, T4 t4) throws Throwable;
  }
}
