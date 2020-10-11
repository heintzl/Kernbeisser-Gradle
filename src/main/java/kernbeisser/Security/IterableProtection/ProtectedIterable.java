package kernbeisser.Security.IterableProtection;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Security.PermissionSet;

public interface ProtectedIterable {

  default boolean isReadable() {
    try {
      checkRead();
      return true;
    } catch (PermissionKeyRequiredException e) {
      return false;
    }
  }

  default boolean isModifiable() {
    try {
      checkWrite();
      return true;
    } catch (PermissionKeyRequiredException e) {
      return false;
    }
  }

  @Key({PermissionKey.READ_ITERABLE_VALUE})
  default void checkRead() {}

  @Key({PermissionKey.MODIFY_ITERABLE_VALUE})
  default void checkWrite() {}

  public static PermissionSet transform(
      PermissionSet ps, PermissionKey read, PermissionKey modify) {
    PermissionSet permissionSet = new PermissionSet();
    if (ps.hasPermission(read)) {
      permissionSet.addPermission(PermissionKey.READ_ITERABLE_VALUE);
    }
    if (ps.hasPermission(modify)) {
      permissionSet.addPermission(PermissionKey.MODIFY_ITERABLE_VALUE);
    }
    return permissionSet;
  }
}
