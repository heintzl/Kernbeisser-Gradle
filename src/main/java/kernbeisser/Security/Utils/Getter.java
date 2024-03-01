package kernbeisser.Security.Utils;

import java.io.Serializable;
import rs.groump.AccessDeniedException;

public interface Getter<P, V> extends Serializable {
  V get(P p) throws AccessDeniedException;
}
