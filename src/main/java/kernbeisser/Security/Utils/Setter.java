package kernbeisser.Security.Utils;

import java.io.Serializable;
import kernbeisser.Exeptions.PermissionKeyRequiredException;

public interface Setter<P, V> extends Serializable {
  void set(P p, V t) throws PermissionKeyRequiredException;
}
