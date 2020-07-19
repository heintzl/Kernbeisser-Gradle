package kernbeisser.CustomComponents;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.LogIn.LogInModel;

public interface RequiresPermission {

  void setReadable(boolean b);

  void setWriteable(boolean b);

  default void setRequiredKeys(PermissionKey read, PermissionKey write) {
    setReadable(LogInModel.getLoggedIn().hasPermission(read));
    setWriteable(LogInModel.getLoggedIn().hasPermission(write));
  }

  default void setReadWrite(PermissionKey key) {
    if (key.name().endsWith("READ")) {
      setRequiredKeys(PermissionKey.valueOf(key.name().replace("READ", "WRITE")), key);
    } else if (key.name().endsWith("WRITE")) {
      setRequiredKeys(key, PermissionKey.valueOf(key.name().replace("WRITE", "READ")));
    }
  }

  default void setRequiredWriteKeys(PermissionKey... keys) {
    setWriteable(LogInModel.getLoggedIn().hasPermission(keys));
  }

  default void setRequiredReadKeys(PermissionKey... keys) {
    setReadable(LogInModel.getLoggedIn().hasPermission(keys));
  }
}
