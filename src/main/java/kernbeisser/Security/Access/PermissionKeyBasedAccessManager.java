package kernbeisser.Security.Access;

import kernbeisser.Security.PermissionSet;

public interface PermissionKeyBasedAccessManager extends AccessManager {

  boolean hasPermission(PermissionSet keys);
}
