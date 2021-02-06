package kernbeisser.Security.Utils;

import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface Setter<P, V> {
  void set(P p, V t) throws PermissionKeyRequiredException;
}
