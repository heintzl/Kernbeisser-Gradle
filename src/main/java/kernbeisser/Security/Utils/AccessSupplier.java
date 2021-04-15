package kernbeisser.Security.Utils;

import java.io.Serializable;
import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface AccessSupplier<T> extends Serializable {
  T get() throws PermissionKeyRequiredException;
}
