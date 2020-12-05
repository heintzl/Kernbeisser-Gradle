package kernbeisser.Windows.SynchronizeArticles;

import kernbeisser.CustomComponents.AccessChecking.Getter;
import kernbeisser.CustomComponents.AccessChecking.Setter;

public interface Difference<P, V> extends Getter<P, V>, Setter<P, V> {
  String getName();

  double distance(P a, P b);

  default boolean equal(P a, P b) {
    return get(a).equals(get(b));
  }

  default void transfer(P from, P to) {
    set(to, get(from));
  }
}
