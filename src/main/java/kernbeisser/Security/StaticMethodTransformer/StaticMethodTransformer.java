package kernbeisser.Security.StaticMethodTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import kernbeisser.Security.MethodListener;
import kernbeisser.Useful.Tools;

public class StaticMethodTransformer <T> implements InvocationHandler {

  private final T fakeInstance;


  public StaticMethodTransformer(Class<T> target) throws InstantiationException {
    fakeInstance = kernbeisser.Security.Proxy.injectMethodHandler(
        Tools.createWithoutConstructor(target), new MethodListener() {
          @Override
          public void preMethod(Method original) {
            if(!original.isAnnotationPresent(StaticAccessPoint.class)){
              throw new UnsupportedOperationException("cannot call method "+original+" from static context");
            }
          }

          @Override
          public void afterMethod(Method original) {}
        });
  }


  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.isAnnotationPresent(StaticAccessPoint.class)) {
      return method.invoke(fakeInstance,args);
    }else throw new UnsupportedOperationException("function: "+method+" does not support calling from static scope, annotate with @StaticAccessPoint");

  }

  public static <T extends StaticInterface> T createStaticInterface(Class<T> interfaceClass,Class<? extends T> target){
    try {
      return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{interfaceClass}, new StaticMethodTransformer(
          target));
    } catch (InstantiationException e) {
      System.exit(-4);
      return null;
    }
  }
}
