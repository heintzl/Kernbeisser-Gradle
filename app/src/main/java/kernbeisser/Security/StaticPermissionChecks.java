package kernbeisser.Security;

import lombok.Getter;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class StaticPermissionChecks {

  @Getter private static final StaticPermissionChecks staticInstance = new StaticPermissionChecks();

  @Key(PermissionKey.ACTION_GRANT_CASHIER_PERMISSION)
  public void checkActionGrantCashierPermission() {}

  @Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)
  public void checkShouldReadArticlePrintPoolAgain() {}
}
