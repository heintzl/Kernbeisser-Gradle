package kernbeisser.Exeptions;

public class PermissionKeyRequiredException extends ProxyException {
  public PermissionKeyRequiredException(String message) {
    super(message);
  }

  public PermissionKeyRequiredException() {}
}
