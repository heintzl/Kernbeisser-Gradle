package kernbeisser.VersionIntegrationTools;

public class VersionUpdatingException extends RuntimeException {

  public VersionUpdatingException() {}

  public VersionUpdatingException(String message) {
    super(message);
  }

  public VersionUpdatingException(String message, Throwable cause) {
    super(message, cause);
  }

  public VersionUpdatingException(Throwable cause) {
    super(cause);
  }

  public VersionUpdatingException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
