package rs.groump;

public class AccessDeniedException extends RuntimeException{
  public AccessDeniedException(String message) {
    super(message);
  }

  public AccessDeniedException() {}
}
