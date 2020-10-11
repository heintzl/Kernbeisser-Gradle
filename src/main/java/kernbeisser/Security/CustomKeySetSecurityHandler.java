package kernbeisser.Security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import javassist.util.proxy.MethodHandler;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.LogIn.LogInModel;

public class CustomKeySetSecurityHandler implements MethodHandler {

  private final PermissionSet permissionSet;

  public CustomKeySetSecurityHandler(PermissionSet keys) {
    this.permissionSet = keys;
  }

  public CustomKeySetSecurityHandler(PermissionKey... keys) {
    this(new PermissionSet());
    for (PermissionKey key : keys) {
      permissionSet.addPermission(key);
    }
  }

  public Object invoke(Object proxy, Method proxyMethod, Method original, Object[] args)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
          AccessDeniedException {
    Key key = proxyMethod.getAnnotation(Key.class);
    Object out;
    if (key == null || permissionSet.hasPermissions(key.value())) {
      out = original.invoke(proxy, args);
    } else {
      throw new AccessDeniedException(
          "User["
              + LogInModel.getLoggedIn().getIdWithoutPermission()
              + "] cannot access "
              + original
              + " because the user has not the required Keys:"
              + Arrays.toString(key.value()));
    }
    return out;
  }
}
