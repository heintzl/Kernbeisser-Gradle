package kernbeisser.Enums;

import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Security.Access.UserRelatedAccessManager;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.Getter;

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
        AccessManager.NO_ACCESS_CHECKING,
        () ->
            UserRelatedAccessManager.ofUser(LogInModel.getLoggedIn()).contains(writePermissionKey));
  }
}
