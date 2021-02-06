package kernbeisser.Tasks.Catalog.Merge;

import kernbeisser.Security.Utils.Getter;
import kernbeisser.Security.Utils.Setter;

public interface Difference<P, V> extends Getter<P, V>, Setter<P, V> {
  String getName();

  double distance(V a, V b);

  default boolean equal(V a, V b) {
    return a.equals(b);
  }

  default void transfer(P from, P to) {
    set(to, get(from));
  }
}
