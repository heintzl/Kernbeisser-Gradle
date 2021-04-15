package kernbeisser.Unsafe;

import java.lang.reflect.Method;
import lombok.Data;

@Data
public class AnalyserTarget {

  private final String targetMethodName, targetDescriptor;
  private final Class<?> targetClass;

  public AnalyserTarget(String targetMethodName, String targetDescriptor, Class<?> targetClass) {
    this.targetMethodName = targetMethodName;
    this.targetDescriptor = targetDescriptor;
    this.targetClass = targetClass;
  }

  public static AnalyserTarget ofMethod(Method method) {
    return new AnalyserTarget(
        method.getName(),
        PermissionKeyMethodVisitor.getSignature(method),
        method.getDeclaringClass());
  }
}
