package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface Getter<P, V> {
  V get(P p) throws PermissionKeyRequiredException;
}
