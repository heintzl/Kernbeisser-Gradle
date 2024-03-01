package kernbeisser.Security.Access;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Useful.WeakReferenceMap;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import rs.groump.AccessManager;
import rs.groump.PermissionSet;

public class UserRelatedAccessManager implements AccessManager {

  @Setter private User targetUser;

  private final PermissionSet inRelation;

  private final WeakReferenceMap<Object, AccessManager> exceptions = new WeakReferenceMap<>();

  private final PermissionSet userPermissions;

  public UserRelatedAccessManager(@NotNull User targetUser) {
    this.targetUser = targetUser;
    userPermissions = targetUser.getPermissionSet();
    inRelation = PermissionConstants.IN_RELATION_TO_OWN_USER.getPermission().toPermissionSet();
  }

  @Override
  public boolean hasAccess(Object object, PermissionSet keys) {
    return userPermissions.contains(keys)
        || checkUserRelatedPermission(object, keys)
        || checkException(object, keys);
  }

  private boolean checkUserRelatedPermission(Object object, PermissionSet keys) {
    return object instanceof UserRelated
        && ((UserRelated) object).isInRelation(targetUser)
        && inRelation.contains(keys);
  }

  private boolean checkException(Object object, PermissionSet keys) {
    AccessManager exceptionAccessManager = exceptions.get(object);
    return exceptionAccessManager != null && exceptionAccessManager.hasAccess(object, keys);
  }

  public void registerException(Object o, AccessManager accessManager) {
    exceptions.put(o, accessManager);
  }

  public void removeException(Object o) {
    exceptions.remove(o);
  }
}
