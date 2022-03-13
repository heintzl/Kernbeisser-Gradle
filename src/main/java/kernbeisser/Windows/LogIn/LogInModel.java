package kernbeisser.Windows.LogIn;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Main;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Security.Access.UserRelatedAccessManager;
import kernbeisser.Security.Key;
import kernbeisser.Security.Relations.UserRelated;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

public final class LogInModel implements IModel {

  @Getter private static int loggedInId;
  private static UserRelatedAccessManager userRelatedAccessManager;
  @Getter @Setter private static boolean requiresRefresh = false;

  public static User getLoggedIn() {
    if (loggedInId == -1) throw new RuntimeException("No user is logged in");
    return User.getById(loggedInId);
  }

  public static void logIn(String username, char[] password)
      throws CannotLogInException, PermissionRequired {
    Access.setDefaultManager(AccessManager.NO_ACCESS_CHECKING);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      User user =
          em.createQuery("select u from User u where u.username = :username", User.class)
              .setParameter("username", username)
              .getSingleResult();
      if (BCrypt.verifyer().verify(password, user.getPassword().toCharArray()).verified) {
        loggedInId = user.getId();
        userRelatedAccessManager = new UserRelatedAccessManager(getLoggedIn());
        Access.setDefaultManager(userRelatedAccessManager);
        if (!Tools.canInvoke(() -> new LogInModel().canLogIn())) {
          loggedInId = -1;
          Access.setDefaultManager(AccessManager.ACCESS_DENIED);
          throw new PermissionRequired();
        }
        Main.logger.info("User with user id [" + user.getId() + "] has logged in");
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
