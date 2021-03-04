package kernbeisser.Security.Utils;

import java.io.Serializable;
import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface Getter<P, V> extends Serializable {
  V get(P p) throws PermissionKeyRequiredException;
}
