package kernbeisser.Forms.ObjectForm.Components;

import java.util.Collection;
import java.util.Objects;

public interface Source<T> {
  Collection<T> query();

  default boolean equal(T a, T b) {
    return Objects.equals(a, b);
  }
}
