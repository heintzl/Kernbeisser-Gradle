package kernbeisser.Exeptions;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.PermissionSet;
import lombok.Getter;

public class PermissionKeyRequiredException extends ProxyException {

  @Getter private final PermissionSet set;

  @Getter private final PermissionSet missing;

  private static PermissionSet missing(PermissionSet set, PermissionSet required) {
    required.removeAll(set);
    return required;
  }

  public PermissionKeyRequiredException(
      PermissionSet set, PermissionKey[] required, String methodName) {
    this(set, PermissionSet.asPermissionSet(required), methodName);
  }

  public PermissionKeyRequiredException(
      PermissionSet set, PermissionSet required, String methodName) {
    this(set, required, missing(set, required), methodName);
  }

  public PermissionKeyRequiredException(
      PermissionSet set, PermissionSet required, PermissionSet missing, String methodName) {
    super(
        "\nPermissionSet: "
            + set.toString()
            + "\nhas not all required permissionKeys to run "
            + methodName
            + "\n"
            + "Required keys: "
            + required
            + "\nMissing Keys: "
            + missing);
    this.set = set;
    this.missing = missing;
  }
}
