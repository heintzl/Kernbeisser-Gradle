package kernbeisser.Security.Access;

import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Security.PermissionSet;

public interface AccessManager {

  boolean hasAccess(Object object, String methodName, String signature, PermissionSet keys);

  public static PermissionSet ALL_PERMISSIONS = PermissionConstants.allPermissions();

  public static PermissionKeyBasedAccessManager ACCESS_DENIED =
      new PermissionSetAccessManager(new PermissionSet());
  public static PermissionKeyBasedAccessManager NO_ACCESS_CHECKING =
      new PermissionSetAccessManager(AccessManager.ALL_PERMISSIONS);
}
