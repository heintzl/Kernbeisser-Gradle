package kernbeisser.Security.Utils;

import java.io.Serializable;
import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface AccessConsumer<T> extends Serializable {
  void accept(T t) throws PermissionKeyRequiredException;
}
