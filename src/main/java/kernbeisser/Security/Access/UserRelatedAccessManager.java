package kernbeisser.Security.Access;

import kernbeisser.DBEntities.User;
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
  public boolean hasAccess(Object object, String methodName, String signature, PermissionSet keys) {
    return super.hasAccess(object, methodName, signature, keys)
        || (object instanceof UserRelated
            && targetUser != null
            && ((UserRelated) object).isInRelation(targetUser)
            && PermissionSet.IN_RELATION_TO_USER.contains(keys));
  }
}
