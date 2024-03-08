package kernbeisser.Security.Access;

import static org.junit.jupiter.api.Assertions.*;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionConstants;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import rs.groump.*;

class UserRelatedAccessManagerTest {

  @Test
  void userRelatedAccess() {
    User execute =
        new User() {
          @Override
          public PermissionSet getPermissionSet() {
            PermissionSet ps = new PermissionSet();
            ps.setAllBits(false);
            return ps;
          }
        };
    User related =
        new User() {
          @Override
          public boolean isInRelation(@NotNull User user) {
            return execute == user;
          }
        };
    UserRelatedAccessManager userRelatedAccessManager = new UserRelatedAccessManager(execute);
    PermissionSet userRelated =
        Access.runUnchecked(
            PermissionConstants.IN_RELATION_TO_OWN_USER.getPermission()::toPermissionSet);
    Access.runWithAccessManager(
        userRelatedAccessManager,
        () -> {
          AccessManager accessManager = Access.getAccessManager();
          assertTrue(accessManager.hasAccess(related, userRelated));
          PermissionSet full = new PermissionSet();
          full.setAllBits(true);
          PermissionSet allOtherThanUserRelated = full.minus(userRelated);
          assertFalse(accessManager.hasAccess(related, allOtherThanUserRelated));
        });
  }
}
