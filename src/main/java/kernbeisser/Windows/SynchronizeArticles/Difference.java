package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.CustomComponents.AccessChecking.Getter;
import kernbeisser.CustomComponents.AccessChecking.Setter;

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
