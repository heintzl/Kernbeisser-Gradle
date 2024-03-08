package kernbeisser.Security.Utils;

import java.io.Serializable;
import rs.groump.AccessDeniedException;

public interface AccessSupplier<T> extends Serializable {
  T get() throws AccessDeniedException;
}
