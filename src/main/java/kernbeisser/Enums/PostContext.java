package kernbeisser.Enums;

import kernbeisser.Windows.LogIn.LogInModel;
import lombok.Getter;
import rs.groump.Access;
import rs.groump.AccessManager;
import rs.groump.PermissionKey;

public enum PostContext {
  ON_SALE_SESSION_CLOSE("Ladendienst-Ende", PermissionKey.POST_ON_SALE_SESSION_CLOSE);

  @Getter private final String title;
  @Getter private final PermissionKey writePermissionKey;

  private PostContext(String title, PermissionKey writePermissionKey) {
    this.title = title;
    this.writePermissionKey = writePermissionKey;
  }

  public boolean isWriteable() {
    return Access.runWithAccessManager(
        AccessManager.ACCESS_GRANTED,
        () -> LogInModel.getLoggedIn().getPermissionSet().contains(writePermissionKey));
  }
}
