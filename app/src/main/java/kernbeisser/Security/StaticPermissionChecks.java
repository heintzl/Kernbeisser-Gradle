package kernbeisser.Security;

import lombok.Getter;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class StaticPermissionChecks {

  @Getter private static final StaticPermissionChecks staticInstance = new StaticPermissionChecks();

  @Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)
  public void checkShouldReadArticlePrintPoolAgain() {}

  @Key(PermissionKey.ACTION_PREORDER_DISCOUNT)
  public void checkPreorderDiscountPermission() {}

  @Key(PermissionKey.ACTION_OPEN_OWN_PRE_ORDER)
  public void checkOwnPreorderPermission() {}

  @Key(PermissionKey.ACTION_ORDER_OWN_CONTAINER)
  public void checkOrderOwnContainerPermission() {}

  @Key(PermissionKey.ACTION_ORDER_CONTAINER)
  public void checkOrderContainerPermission() {}
}
