package kernbeisser.Forms.ObjectForm;

import kernbeisser.Enums.Mode;
import kernbeisser.Forms.ObjectForm.Exceptions.CannotParseException;

public interface ComparingObjectValidator<T> {
  void validate(T original, T input, Mode mode) throws CannotParseException;

  default void invalidNotifier() {}
}
