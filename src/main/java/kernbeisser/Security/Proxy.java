package kernbeisser.Security;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;
import kernbeisser.Unsafe.PermissionKeyMethodVisitor;
import kernbeisser.Useful.Tools;
import lombok.SneakyThrows;

public class Proxy {

  private static final HashMap<Class<?>, Class<?>> proxyClassCache = new HashMap<>();

  /**
   * creates a empty instance of a security checked object, which is used to test functions for
   * accessibility
   *
   * @param clazz the clazz for the Object
   * @param <T> the type of the secure instance
   * @return a empty proxy object
   */
  public static <T> T getEmptySecurityInstance(Class<T> clazz) {
    if (ProxyFactory.isProxyClass(clazz)) {
      return Tools.createWithoutConstructor(clazz);
    }
    return injectMethodHandler(
        Tools.createWithoutConstructor(clazz), PermissionSetSecurityHandler.ON_LOGGED_IN);
  }

  /**
   * wraps a Object with a Proxy which is used to check if the PermissionSet contains the required
   * Keys to run the Function else it throws a AccessDeniedException
   *
   * @param parent the Object which should get wrap into a Proxy Object
   * @param <T> the type of the Object
   * @return a Proxy which extends T
   */
  public static <T> T getSecureInstance(T parent) {
    if (parent == null || ProxyFactory.isProxyClass(parent.getClass())) {
      return parent;
    }
    return injectMethodHandler(parent, PermissionSetSecurityHandler.ON_LOGGED_IN);
  }

  /**
   * do the same like getSecureInstance only for a whole collection
   *
   * @param collection collection with Objects
   * @param <C> the collection with the values
   * @param <V> the type of the Object which gets transformed
   * @return the collection c with Proxy extends v values
   */
  public static <C extends Collection<V>, V> C getSecureInstances(C collection) {
    if (collection.size() == 0) {
      return collection;
    }
    V any = collection.iterator().next();
    if (ProxyFactory.isProxyClass(any.getClass())) {
      return collection;
    }
    Class<?> proxyClass = getProxyClass(any.getClass());
    return (C)
        collection.stream()
            .map(e -> injectMethodHandler(proxyClass, e, PermissionSetSecurityHandler.ON_LOGGED_IN))
            .collect(Collectors.toCollection(ArrayList::new));
  }

  public static Class<?> getProxyClass(Class<?> clazz) {
    Class<?> cachedClass = proxyClassCache.get(clazz);
    if (cachedClass == null) {
      ProxyFactory factory = new ProxyFactory();
      factory.setSuperclass(clazz);
      cachedClass = factory.createClass();
      proxyClassCache.put(clazz, cachedClass);
    }
    return cachedClass;
  }

  private static <T> T injectMethodHandler(
      Class<?> proxyClass, T value, MethodHandler methodHandler) {
    if (value == null) return null;
    javassist.util.proxy.Proxy p =
        (javassist.util.proxy.Proxy) Tools.createWithoutConstructor(proxyClass);
    p.setHandler(methodHandler);
    Tools.copyInto(value, p);
    return (T) p;
  }

  public static <T> T injectMethodHandler(T value, MethodHandler methodHandler) {
    return injectMethodHandler(getProxyClass(value.getClass()), value, methodHandler);
  }

  /**
   * returns if an Object is a ProxyInstance
   *
   * @param o the object
   * @return if the object is a ProxyInstance
   */
  public static boolean isProxyInstance(Object o) {
    return ProxyFactory.isProxyClass(o.getClass());
  }

  /**
   * reutns the SecurityHandler of an specified object
   *
   * @param o the ProxyInstance
   * @return the SecurityHandler from the object
   */
  @SneakyThrows
  public static MethodHandler getHandler(Object o) {
    return ProxyFactory.getHandler((javassist.util.proxy.Proxy) o);
  }

  public static <T, V> boolean hasPermission(Getter<T, V> getter, T parent) {
    if (parent instanceof javassist.util.proxy.Proxy) {
      MethodHandler methodHandler = getHandler(parent);
      if (methodHandler instanceof PermissionSetSecurityHandler) {
        return ((PermissionSetSecurityHandler) methodHandler)
            .getPermissionSet()
            .contains(peekPermissions(getter));
      }
    }
    return true;
  }

  public static <T, V> boolean hasPermission(Setter<T, V> setter, T parent) {
    if (parent instanceof javassist.util.proxy.Proxy) {
      MethodHandler methodHandler = getHandler(parent);
      if (methodHandler instanceof PermissionSetSecurityHandler) {
        return ((PermissionSetSecurityHandler) methodHandler)
            .getPermissionSet()
            .contains(peekPermissions(setter));
      }
    }
    return true;
  }

  public static <T> T removeProxy(T t) {
    if (!isProxyInstance(t)) return t;
    Class<?> proxyClass = t.getClass();
    String normalName = proxyClass.getName().substring(0, proxyClass.getName().indexOf("_"));
    try {
      T out =
          (T) Tools.createWithoutConstructor(t.getClass().getClassLoader().loadClass(normalName));
      Tools.copyInto(t, out);
      return out;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return t;
    }
  }

  /**
   * peeks the into the byte code of the specified method and all sub methods. It searches for
   * PermissionKey annotations and collect them in a permissionSet, the result is permissionSet with
   * all possible required permissions. //TODO: maybe soft cache the results of the scans
   */
  @SneakyThrows
  public static PermissionSet peekPermissions(Serializable interfaceLambdaImp) {
    return PermissionKeyMethodVisitor.accessedKeys(interfaceLambdaImp);
  }
}
