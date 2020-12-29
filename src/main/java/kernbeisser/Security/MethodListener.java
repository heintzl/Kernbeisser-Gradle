package kernbeisser.Security;

import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;

public interface MethodListener extends MethodHandler {

  default void preMethod(Method original) {}

  void afterMethod(Method original);

  @Override
  default Object invoke(Object proxy, Method proxyMethod, Method original, Object[] args)
      throws Throwable {
    preMethod(proxyMethod);
    Object out = original.invoke(proxy, args);
    afterMethod(proxyMethod);
    return out;
  }
}
