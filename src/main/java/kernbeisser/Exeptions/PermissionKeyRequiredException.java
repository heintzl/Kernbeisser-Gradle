package kernbeisser.Exeptions;

import kernbeisser.Security.PermissionSet;

public class PermissionKeyRequiredException extends ProxyException {
  public PermissionKeyRequiredException(String message) {
    super(message);
  }

  public PermissionKeyRequiredException() {}

  public PermissionSet calculateMissingKeys() {
    System.out.println(getStackTrace()[5]);

    return new PermissionSet();
  }
}
