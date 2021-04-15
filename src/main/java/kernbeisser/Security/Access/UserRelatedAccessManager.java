package kernbeisser.Security.Access;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.Relations.UserRelated;
import org.jetbrains.annotations.NotNull;

public class UserRelatedAccessManager extends PermissionSetAccessManager {

  private final User targetUser;

  private final PermissionSet inRelation;

  public static PermissionSet ofUser(User user) {
    PermissionSet ps = new PermissionSet();
    ps.loadPermission(user.getPermissions());
    return ps;
  }

  public UserRelatedAccessManager(@NotNull User targetUser) {
    super(ofUser(targetUser));
    this.targetUser = targetUser;
    inRelation =
        PermissionSet.ofPermission(PermissionConstants.IN_RELATION_TO_OWN_USER.getPermission());
  }

  @Override
  public boolean hasAccess(Object object, String methodName, String signature, PermissionSet keys) {
    return super.hasAccess(object, methodName, signature, keys)
        || (object instanceof UserRelated
            && targetUser != null
            && ((UserRelated) object).isInRelation(targetUser)
            && inRelation.contains(keys));
  }
}
