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
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public final class LogInModel implements IModel {

  private static User loggedIn;

  public static User getLoggedIn() {
    return loggedIn;
  }

  public static User getLoggedInFromDB() {
    loggedIn = (User.getById(loggedIn.getId()));
    Access.setDefaultManager(AccessManager.NO_ACCESS_CHECKING);
    Access.setDefaultManager(new UserRelatedAccessManager(loggedIn));
    return loggedIn;
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
        loggedIn = user;
        Access.setDefaultManager(new UserRelatedAccessManager(loggedIn));
        if (!Tools.canInvoke(() -> new LogInModel().canLogIn())) {
          loggedIn = null;
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

  @Key(PermissionKey.ACTION_LOGIN)
  private void canLogIn() {}
}
