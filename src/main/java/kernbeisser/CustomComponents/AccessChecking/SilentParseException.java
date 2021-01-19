package kernbeisser.CustomComponents.AccessChecking;

import kernbeisser.Exeptions.CannotParseException;

public class SilentParseException extends CannotParseException {

  public SilentParseException(String s) {
    super(s);
  }

  public SilentParseException() {
    super();
  }
}
