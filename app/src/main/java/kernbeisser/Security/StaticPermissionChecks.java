package kernbeisser.Security;

import lombok.Getter;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class StaticPermissionChecks {

  @Getter private static final StaticPermissionChecks staticInstance = new StaticPermissionChecks();

  @Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)
  public void checkShouldReadArticlePrintPoolAgain() {}

  @Key(PermissionKey.ACTION_PREORDER_DISCOUNT)
  public void checkPreorderPermission() {}
}
