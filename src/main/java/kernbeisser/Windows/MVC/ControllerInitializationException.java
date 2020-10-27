package kernbeisser.Windows.MVC;

public class ControllerInitializationException extends RuntimeException {
  public ControllerInitializationException() {
    super();
  }

  public ControllerInitializationException(String message) {
    super(message);
  }

  public ControllerInitializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ControllerInitializationException(Throwable cause) {
    super(cause);
  }

  protected ControllerInitializationException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
