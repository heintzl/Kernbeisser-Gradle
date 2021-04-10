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
import kernbeisser.Security.PermissionSet;
import lombok.Cleanup;

public class LogInModel {

  public static User loggedIn;

  public static void refreshLogInData() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    loggedIn = em.find(loggedIn.getClass(), loggedIn.getId());
  }

  public LogInModel() {
    loggedIn = null;
  }

  public static User getLoggedIn() {
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
        if (!user.hasPermission(PermissionKey.ACTION_LOGIN)) {
          throw new PermissionRequired();
        }
        loggedIn = user;
        PermissionSet.MASTER.loadPermission(loggedIn.getPermissions());
        Access.setDefaultManager(new UserRelatedAccessManager(loggedIn));

        Main.logger.info("User with user id [" + user.getId() + "] has logged in");
      } else {
        throw new CannotLogInException();
      }
    } catch (NoResultException e) {
      throw new CannotLogInException();
    }
  }

  public static boolean isValidLogIn(String username, char[] password) {
    return BCrypt.verifyer()
        .verify(password, User.getByUsername(username).getPassword().toCharArray())
        .verified;
  }
}
