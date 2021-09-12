package kernbeisser.Exeptions;

public class InvalidValue extends Exception {
  public InvalidValue() {
    super();
  }

  public InvalidValue(String message) {
    super(message);
  }

  public InvalidValue(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidValue(Throwable cause) {
    super(cause);
  }

  protected InvalidValue(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
