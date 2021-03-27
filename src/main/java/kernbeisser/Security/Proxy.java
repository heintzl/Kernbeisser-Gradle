package kernbeisser.Security;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;
import kernbeisser.Unsafe.PermissionKeyMethodVisitor;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.SneakyThrows;

public class Proxy {

  private static final HashMap<Class<?>, Class<?>> proxyClassCache = new HashMap<>();

  /**
   * converts stream elements into protected instances
   *
   * @param stream the object stream
   * @param clazz the class of the proxy
   * @param <T> the type of the proxy elements
   * @return a stream with proxyfied elements
   */
  public static <T> Stream<? extends T> securedStream(Stream<T> stream, Class<T> clazz) {
    Class<? extends T> proxyClass = getProxyClass(clazz);
    MethodHandler mh = PermissionSetSecurityHandler.ON_LOGGED_IN;
    return stream.map(e -> injectMethodHandler(proxyClass, e, mh));
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
    ArrayList<V> buffer = getSecureInstances(new ArrayList<>(collection));
    collection.clear();
    collection.addAll(buffer);
    return collection;
  }

  /** same as #getSecureInstances(Collection<T>) but faster list based impl. */
  public static <C extends List<V>, V> C getSecureInstances(C collection) {
    try {
      Class<?> proxyClass = getProxyClass(obtainClass(collection));
      collection.replaceAll(
          e -> injectMethodHandler(proxyClass, e, PermissionSetSecurityHandler.ON_LOGGED_IN));
    } catch (UnsupportedOperationException ignored) {
    }
    return collection;
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

  /** refreshes the object and ignores if the class is an instance of proxy */
  public static <T> T refresh(T t) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    if (isProxyInstance(t)) {
      Class<?> superClass = t.getClass().getSuperclass();
      T unProxy = (T) em.find(superClass, Tools.getId(t));
      return getSecureInstance(unProxy);
    }
    return t;
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
