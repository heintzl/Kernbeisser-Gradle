package kernbeisser.Exeptions;

public class PermissionKeyRequiredException extends ProxyException {
  public PermissionKeyRequiredException() {}

  public PermissionKeyRequiredException(String message) {
    super(message);
  }
}
