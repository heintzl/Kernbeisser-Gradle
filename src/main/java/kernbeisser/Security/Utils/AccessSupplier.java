package kernbeisser.Security.Utils;

import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface AccessSupplier<T> {
  T get() throws PermissionKeyRequiredException;
}
