package kernbeisser.Security.MethodHandlers;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javassist.util.proxy.MethodHandler;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.IterableProtection.CollectionProxy;
import kernbeisser.Security.IterableProtection.MapProxy;
import kernbeisser.Security.IterableProtection.ProxyIterable;
import kernbeisser.Security.IterableProtection.SetProxy;
import kernbeisser.Security.Key;
import kernbeisser.Security.Proxy;
import lombok.SneakyThrows;

public abstract class AbstractSecurityHandler implements MethodHandler {

  protected abstract boolean canInvoke(PermissionKey[] keys);

  protected abstract RuntimeException accessDenied(PermissionKey[] keys, Method method);

  @SneakyThrows
  protected Object handleKeyAnnotation(Key key, Object proxy, Method original, Object[] args) {
    if (canInvoke(key.value())) {
      return proxyChild(original.invoke(proxy, args));
    } else throw accessDenied(key.value(), original);
  }

  @SneakyThrows
  protected Object handleProxyIterableAnnotation(
      ProxyIterable proxyIterable, Object proxy, Method original, Object[] args) {
    return wrapMapOrIterable(
        original.invoke(proxy, args), proxyIterable.read(), proxyIterable.modify());
  }

  @SneakyThrows
  protected Object handleUnprotectedMethod(Object proxy, Method original, Object[] args) {
    return original.invoke(proxy, args);
  }

  public Object invoke(Object proxy, Method proxyMethod, Method original, Object[] args)
      throws IllegalArgumentException {

    Key key = proxyMethod.getAnnotation(Key.class);
    if (key != null) {
      return handleKeyAnnotation(key, proxy, original, args);
    }

    ProxyIterable proxyIterable = proxyMethod.getAnnotation(ProxyIterable.class);
    return proxyIterable != null
        ? handleProxyIterableAnnotation(proxyIterable, proxy, original, args)
        : handleUnprotectedMethod(proxy, original, args);
  }

  protected Object proxyChild(Object o) {
    return Proxy.injectMethodHandler(o, this);
  }

  private Object wrapMapOrIterable(Object in, PermissionKey[] read, PermissionKey[] modify) {
    if (in == null) return null;
    if (Set.class.isAssignableFrom(in.getClass()))
      return SetProxy.create((Set<?>) in, read, modify, this);
    if (Collection.class.isAssignableFrom(in.getClass()))
      return CollectionProxy.create((Collection<?>) in, read, modify, this);
    if (Map.class.isAssignableFrom(in.getClass()))
      return MapProxy.create((Map<?, ?>) in, read, modify, this);
    throw new UnsupportedOperationException(
        "cannot warp "
            + in
            + " with [collection | set | map] consider using @Key for normal objects");
  }
}
