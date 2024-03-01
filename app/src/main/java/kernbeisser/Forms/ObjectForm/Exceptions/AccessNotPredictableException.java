package kernbeisser.Forms.ObjectForm.Exceptions;

public class AccessNotPredictableException extends RuntimeException {

  public AccessNotPredictableException() {
    super();
  }

  public AccessNotPredictableException(String message) {
    super(message);
  }

  public AccessNotPredictableException(String message, Throwable cause) {
    super(message, cause);
  }

  public AccessNotPredictableException(Throwable cause) {
    super(cause);
  }

  protected AccessNotPredictableException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
