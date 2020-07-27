package kernbeisser.Exeptions;

public class AccessDeniedException extends Exception {
  public AccessDeniedException(String message) {
    super(message);
  }

  public AccessDeniedException() {}
}
