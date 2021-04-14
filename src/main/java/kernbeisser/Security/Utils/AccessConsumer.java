package kernbeisser.Security.Utils;

import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface AccessConsumer<T> {
  void accept(T t) throws PermissionKeyRequiredException;
}
