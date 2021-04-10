package kernbeisser.Security;

import java.util.*;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import kernbeisser.Useful.Tools;
import lombok.SneakyThrows;

public class Proxy {

  private static final HashMap<Class<?>, Class<?>> proxyClassCache = new HashMap<>();

  /**
   * do the same like getSecureInstance only for a whole collection
   *
   * @param collection collection with Objects
   * @param <C> the collection with the values
   * @param <V> the type of the Object which gets transformed
   * @return the collection c with Proxy extends v values
   */
  public static <C extends Collection<V>, V> C injectMultipleMethodHandlers(
      C collection, MethodHandler methodHandler) {
    ArrayList<V> buffer = injectMultipleMethodHandlers(new ArrayList<>(collection), methodHandler);
    collection.clear();
    collection.addAll(buffer);
    return collection;
  }

  /** same as #getSecureInstances(Collection<T>) but faster list based impl. */
  public static <L extends List<V>, V> L injectMultipleMethodHandlers(
      L list, MethodHandler methodHandler) {
    try {
      Class<?> proxyClass = getProxyClass(obtainClass(list));
      list.replaceAll(e -> injectMethodHandler(proxyClass, e, methodHandler));
    } catch (UnsupportedOperationException ignored) {
    }
    return list;
  }

  public static <V> Class<V> obtainClass(Collection<V> collection) {
    return (Class<V>)
        collection.stream()
            .findAny()
            .map(Object::getClass)
            .orElseThrow(UnsupportedOperationException::new);
  }

  public static <T> Class<? extends T> getProxyClass(Class<T> clazz) {
    Class<? extends T> cachedClass = (Class<? extends T>) proxyClassCache.get(clazz);
    if (cachedClass == null) {
      ProxyFactory factory = new ProxyFactory();
      factory.setSuperclass(clazz);
      cachedClass = (Class<? extends T>) factory.createClass();
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
}
