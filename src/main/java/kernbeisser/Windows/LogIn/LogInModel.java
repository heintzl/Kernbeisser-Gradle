package kernbeisser.Windows.LogIn;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Main;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.PermissionSetSecurityHandler;
import kernbeisser.Security.Proxy;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class LogInModel implements IModel {

  public static User loggedIn;

  public static void refreshLogInData() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    loggedIn =
        Proxy.injectMethodHandler(
            em.find(User.class, loggedIn.getId()),
            new PermissionSetSecurityHandler(
                PermissionConstants.ON_OWN_USER
                    .getPermission()
                    .getKeySet()
                    .toArray(new PermissionKey[0])));
    ;
  }

  public LogInModel() {
    loggedIn = null;
  }

  public static User getLoggedIn() {
    return loggedIn;
  }

  public static void logIn(String username, char[] password)
      throws CannotLogInException, PermissionRequired {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      User user =
          em.createQuery("select u from User u where u.username = :username", User.class)
              .setParameter("username", username)
              .getResultList()
              .get(0);
      if (BCrypt.verifyer().verify(password, user.getPassword().toCharArray()).verified) {
        if (!user.hasPermission(PermissionKey.ACTION_LOGIN)) {
          throw new PermissionRequired();
        }
        loggedIn =
            Proxy.injectMethodHandler(
                user,
                new PermissionSetSecurityHandler(
                    PermissionConstants.ON_OWN_USER
                        .getPermission()
                        .getKeySet()
                        .toArray(new PermissionKey[0])));
        PermissionSet.MASTER.loadPermission(user.getPermissions());
        Main.logger.info("User with user id [" + user.getId() + "] has logged in");
      } else {
        throw new CannotLogInException();
      }
    } catch (NoResultException e) {
      throw new CannotLogInException();
    }
  }
}
