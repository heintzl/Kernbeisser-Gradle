package kernbeisser.Enums;

import java.util.function.Supplier;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;

/** used to map PermissionKey[] which should become parsed through an annotation */
public enum KeyCollection implements Supplier<PermissionKey[]> {
  ALL_USER_READ {
    @Override
    public PermissionKey[] get() {
      return PermissionKey.allReadPermissions(User.class);
    }
  },
  ALL_USER_WRITE {
    @Override
    public PermissionKey[] get() {
      return PermissionKey.allWritePermissions(User.class);
    }
  },
  ALL_PERMISSION_READ {
    @Override
    public PermissionKey[] get() {
      return PermissionKey.allReadPermissions(User.class);
    }
  },
  ALL_PERMISSION_WRITE {
    @Override
    public PermissionKey[] get() {
      return PermissionKey.allWritePermissions(Permission.class);
    }
  };
}
