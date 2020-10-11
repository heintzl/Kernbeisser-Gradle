package kernbeisser.Security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javassist.util.proxy.MethodHandler;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Exeptions.ProximateException;
import kernbeisser.Security.IterableProtection.CollectionProxy;
import kernbeisser.Security.IterableProtection.MapProxy;
import kernbeisser.Security.IterableProtection.SetProxy;
import kernbeisser.Windows.LogIn.LogInModel;

public class PermissionSetSecurityHandler implements MethodHandler {

  public static PermissionSetSecurityHandler ON_LOGGED_IN =
      new PermissionSetSecurityHandler(PermissionSet.MASTER);

  private final PermissionSet permissionSet;

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

    if (key == null || permissionSet.hasPermissions(key.value())) {
      Object out = original.invoke(proxy, args);
      try {
        if (out == null) return null;
        if (Set.class.isAssignableFrom(out.getClass()))
          return SetProxy.create((Set) out, permissionSet, key.value()[0], key.value()[1]);
        if (Collection.class.isAssignableFrom(out.getClass()))
          return CollectionProxy.create(
              (Collection) out, permissionSet, key.value()[0], key.value()[1]);
        if (Map.class.isAssignableFrom(out.getClass()))
          return MapProxy.create((Map) out, permissionSet, key.value()[0], key.value()[1], true);
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new ProximateException(
            "key annotation on [" + proxyMethod + "] doesn't contain enough keys");
      }
      return out;

    } else {
      throw new PermissionKeyRequiredException(
          "User["
              + LogInModel.getLoggedIn().getId()
              + "] cannot access "
              + original
              + " because the user has not the required Keys:"
              + Arrays.toString(key.value()));
    }
  }
}
