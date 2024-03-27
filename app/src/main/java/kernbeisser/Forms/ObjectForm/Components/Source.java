package kernbeisser.Forms.ObjectForm.Components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import kernbeisser.DBConnection.QueryBuilder;

public interface Source<T> {
  Collection<T> query();

  default boolean equal(T a, T b) {
    return Objects.equals(a, b);
  }

  public static <T> Source<T> of(Class<T> clazz) {
    return () -> QueryBuilder.selectAll(clazz).getResultList();
  }

  public static <T> Source<T> empty() {
    return () -> new ArrayList<>(0);
  }
}
