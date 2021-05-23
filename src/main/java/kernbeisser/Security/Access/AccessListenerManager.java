package kernbeisser.Security.Access;

import kernbeisser.Security.PermissionSet;
import lombok.Getter;

public class AccessListenerManager implements AccessManager {

  @Getter private boolean success = true;

  private final AccessManager listener;

  public AccessListenerManager(AccessManager accessManager) {
    this.listener = accessManager;
  }

  @Override
  public boolean hasAccess(Object object, String methodName, String signature, PermissionSet keys) {
    success = success && listener.hasAccess(object, methodName, signature, keys);
    return true;
  }
}
