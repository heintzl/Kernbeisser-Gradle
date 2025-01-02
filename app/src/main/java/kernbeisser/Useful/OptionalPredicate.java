package kernbeisser.Useful;

import java.util.Optional;

public interface OptionalPredicate<T> {
  Optional<Boolean> optionalTrue(T filterObject);

  default boolean defaultsToFalse(T filterObject) {
    return optionalTrue(filterObject).orElse(false);
  }
}
