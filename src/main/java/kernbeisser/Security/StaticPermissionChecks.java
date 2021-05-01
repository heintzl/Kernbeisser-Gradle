package kernbeisser.Security;

import kernbeisser.Enums.PermissionKey;
import lombok.Getter;

public class StaticPermissionChecks {

  @Getter private static final StaticPermissionChecks staticInstance = new StaticPermissionChecks();

  @Key(PermissionKey.ACTION_GRANT_CASHIER_PERMISSION)
  public void checkActionGrantCashierPermission() {}
}
