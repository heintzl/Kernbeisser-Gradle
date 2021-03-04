package kernbeisser.Security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javassist.util.proxy.MethodHandler;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.IterableProtection.CollectionProxy;
import kernbeisser.Security.IterableProtection.MapProxy;
import kernbeisser.Security.IterableProtection.ProxyIterable;
import kernbeisser.Security.IterableProtection.SetProxy;
import lombok.Getter;

public class PermissionSetSecurityHandler implements MethodHandler {

  public static PermissionSetSecurityHandler ON_LOGGED_IN =
      new PermissionSetSecurityHandler(PermissionSet.MASTER);

  @Getter private final PermissionSet permissionSet;

  public PermissionSetSecurityHandler(PermissionSet keys) {
    this.permissionSet = keys;
  }

  public PermissionSetSecurityHandler(PermissionKey... keys) {
    this(new PermissionSet());
    for (PermissionKey key : keys) {
      permissionSet.addPermission(key);
    }
  }

  public Object invoke(Object proxy, Method proxyMethod, Method original, Object[] args)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Key key = proxyMethod.getAnnotation(Key.class);
    if (key != null) {
      if (permissionSet.hasPermissions(key.value())) {
        return original.invoke(proxy, args);
      } else
        throw new PermissionKeyRequiredException(
            "permissionSet doesn't contains the required keys: " + Arrays.toString(key.value()));
    }
    ProxyIterable proxyIterable = proxyMethod.getAnnotation(ProxyIterable.class);
    return proxyIterable != null
        ? tryWrapWithProxyLayer(
            original.invoke(proxy, args), proxyIterable.read(), proxyIterable.modify())
        : original.invoke(proxy, args);
  }

  private Object tryWrapWithProxyLayer(Object in, PermissionKey[] read, PermissionKey[] modify) {
    if (in == null) return null;
    if (Set.class.isAssignableFrom(in.getClass()))
      return SetProxy.create((Set<?>) in, permissionSet, read, modify);
    if (Collection.class.isAssignableFrom(in.getClass()))
      return CollectionProxy.create((Collection<?>) in, permissionSet, read, modify);
    if (Map.class.isAssignableFrom(in.getClass()))
      return MapProxy.create((Map<?, ?>) in, permissionSet, read, modify, true);
    throw new UnsupportedOperationException(
        "cannot warp "
            + in
            + " with [collection | set | map] consider using @Key for normal objects");
  }
}
