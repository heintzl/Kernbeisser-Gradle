package kernbeisser.Security.Access;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.Relations.UserRelated;
import org.jetbrains.annotations.NotNull;

public class UserRelatedAccessManager extends PermissionSetAccessManager {

  private final User targetUser;

  public UserRelatedAccessManager(@NotNull User targetUser) {
    super(PermissionSet.MASTER);
    this.targetUser = targetUser;
  }

  @Override
  public boolean hasAccess(
      Object object, String methodName, String signature, PermissionKey[] keys) {
    return super.hasAccess(object, methodName, signature, keys)
        || (object instanceof UserRelated
            && targetUser != null
            && ((UserRelated) object).isInRelation(targetUser)
            && PermissionSet.IN_RELATION_TO_USER.hasPermissions(keys));
  }
}
