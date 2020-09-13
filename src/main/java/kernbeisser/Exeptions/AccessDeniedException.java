package kernbeisser.Exeptions;

public class AccessDeniedException extends ProxyException {
  public AccessDeniedException(String message) {
    super(message);
  }
}
