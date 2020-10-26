package kernbeisser.Exeptions;

public class NotEnoughCreditException extends Exception {

  public NotEnoughCreditException() {
    super();
  }

  public NotEnoughCreditException(String message) {
    super(message);
  }

  public NotEnoughCreditException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotEnoughCreditException(Throwable cause) {
    super(cause);
  }

  protected NotEnoughCreditException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
