package kernbeisser.Security.Utils;

import java.io.Serializable;
import rs.groump.AccessDeniedException;

public interface Setter<P, V> extends Serializable {
  void set(P p, V t) throws AccessDeniedException;
}
