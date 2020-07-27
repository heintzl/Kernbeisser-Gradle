package kernbeisser.Security;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import javassist.util.proxy.MethodHandler;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.LogIn.LogInModel;

public class CustomKeySetSecurityHandler implements MethodHandler {

  private final HashSet<PermissionKey> keys;

  public CustomKeySetSecurityHandler(PermissionKey... keys) {
    this.keys = new HashSet<>(Arrays.asList(keys));
  }

  public Object invoke(Object proxy, Method proxyMethod, Method original, Object[] args)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
          AccessDeniedException {
    Key key = proxyMethod.getAnnotation(Key.class);
    Object out;
    if (key == null || containsAll(key.value())) {
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

  private boolean containsAll(PermissionKey[] keys) {
    for (PermissionKey key : keys) {
      if (!this.keys.contains(key)) {
        return false;
      }
    }
    return true;
  }
}
