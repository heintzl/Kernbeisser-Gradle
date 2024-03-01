package kernbeisser.Exeptions;

public class ProximateException extends RuntimeException {
  public ProximateException() {}

  public ProximateException(String message) {
    super(message);
  }

  public ProximateException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProximateException(Throwable cause) {
    super(cause);
  }

  public ProximateException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
