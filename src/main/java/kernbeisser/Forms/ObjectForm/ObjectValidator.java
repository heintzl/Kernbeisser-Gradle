package kernbeisser.Forms.ObjectForm;

import kernbeisser.Enums.Mode;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;

public interface ObjectValidator<T> {
  void validate(T input, Mode mode) throws CannotParseException;

  default void invalidNotifier() {}
}
