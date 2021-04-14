package kernbeisser.Security.IterableProtection;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;

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
}
