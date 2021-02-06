package kernbeisser.Security.Utils;

import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface Getter<P, V> {
  V get(P p) throws PermissionKeyRequiredException;
}
