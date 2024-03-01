package kernbeisser.EntityWrapper;

import kernbeisser.Useful.Tools;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ObjectState<T> {
  @Getter private final T value;
  @Getter private final int revNumber;

  public static <T> ObjectState<T> wrap(T value, int revNumber) {
    return new ObjectState<>(value, revNumber);
  }

  public static <T> ObjectState<T> currentState(T value) {
    Tools.assertPersisted(value);
    return wrap(value, Tools.getNewestRevisionNumber(value));
  }
}
