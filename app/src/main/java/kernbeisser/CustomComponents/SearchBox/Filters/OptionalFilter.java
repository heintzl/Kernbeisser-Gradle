package kernbeisser.CustomComponents.SearchBox.Filters;

import java.util.Optional;

public interface OptionalFilter<T> {
  Optional<Boolean> optionalTrue(T filterObject);

  default boolean defaultsToFalse(T filterObject) {
    return optionalTrue(filterObject).orElse(false);
  }
}
