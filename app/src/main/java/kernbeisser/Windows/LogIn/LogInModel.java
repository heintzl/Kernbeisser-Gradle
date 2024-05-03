package kernbeisser.Windows.LogIn;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.User_;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Security.Access.UserRelated;
import kernbeisser.Security.Access.UserRelatedAccessManager;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import rs.groump.Access;
import rs.groump.AccessManager;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Log4j2
public final class LogInModel {

  @Getter private static int loggedInId;
  private static UserRelatedAccessManager userRelatedAccessManager;
  @Getter @Setter private static boolean requiresRefresh = false;

  public static User getLoggedIn() {
    if (loggedInId == -1) throw new RuntimeException("No user is logged in");
    return User.getById(loggedInId);
  }

  public static void logIn(String username, char[] password)
      throws CannotLogInException, PermissionRequired {
    Access.setAccessManager(AccessManager.ACCESS_GRANTED);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      User user =
          QueryBuilder.selectAll(User.class).where(User_.username.eq(username)).getSingleResult();
      if (BCrypt.verifyer().verify(password, user.getPassword().toCharArray()).verified) {
        loggedInId = user.getId();
        userRelatedAccessManager = new UserRelatedAccessManager(getLoggedIn());
        Access.setAccessManager(userRelatedAccessManager);
        if (!Tools.canInvoke(() -> new LogInModel().canLogIn())) {
          loggedInId = -1;
          Access.setAccessManager(AccessManager.ACCESS_DENIED);
          throw new PermissionRequired();
        }
        log.info("User with user id [" + user.getId() + "] has logged in");
      } else {
        throw new CannotLogInException();
      }
    } catch (NoResultException e) {
      throw new CannotLogInException();
    }
  }

  public static void checkRefreshRequirements(UserRelated... userRelated) {
    User loggedIn = getLoggedIn();
    if (loggedIn == null) return;
    for (UserRelated ur : userRelated) {
      if (ur.isInRelation(loggedIn)) {
        requiresRefresh = true;
      }
    }
  }

  public static void refreshAccessManager() {
    userRelatedAccessManager.setTargetUser(getLoggedIn());
    requiresRefresh = false;
  }

  public static void refreshAccessManagerIfRequired() {
    if (requiresRefresh) {
      refreshAccessManager();
    }
  }

  @Key(PermissionKey.ACTION_LOGIN)
  private void canLogIn() {}
}
