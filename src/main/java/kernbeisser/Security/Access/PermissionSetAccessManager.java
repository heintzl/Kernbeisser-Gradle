package kernbeisser.Security.Access;

import kernbeisser.Security.PermissionSet;
import org.jetbrains.annotations.NotNull;

public class PermissionSetAccessManager implements PermissionKeyBasedAccessManager {

  private final PermissionSet keySet;

  public PermissionSetAccessManager(@NotNull PermissionSet permissions) {
    this.keySet = permissions;
  }

  @Override
  public boolean hasAccess(Object object, String methodName, String signature, PermissionSet keys) {
    return keySet.contains(keys);
  }

  @Override
  public boolean hasPermission(PermissionSet keys) {
    return keySet.contains(keys);
  }
}
