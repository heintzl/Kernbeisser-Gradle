package kernbeisser.Forms.ObjectForm.Components;

import java.util.Collection;
import java.util.Objects;
import kernbeisser.Useful.Tools;

public interface Source<T> {
  Collection<T> query();

  default boolean equal(T a, T b) {
    return Objects.equals(a, b);
  }

  public static <T> Source<T> of(Class<T> clazz) {
    return () -> Tools.getAll(clazz, null);
  }
}
