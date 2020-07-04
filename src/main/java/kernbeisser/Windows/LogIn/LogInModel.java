package kernbeisser.Windows.LogIn;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Main;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;

public class LogInModel implements Model {

    public static User loggedIn;

    public LogInModel() {
        loggedIn = null;
    }

    public static User getLoggedIn() {
        return loggedIn;
    }

    public static void logIn(String username, char[] password) throws AccessDeniedException, PermissionRequired {
        EntityManager em = DBConnection.getEntityManager();
        try {
            User user = em.createQuery(
                    "select u from User u where u.username like :username", User.class)
                          .setParameter("username", username).
                                  getSingleResult();
            if ( BCrypt.verifyer().verify(password, user.getPassword().toCharArray()).verified) {
                if(!user.hasPermission(Key.ACTION_LOGIN))throw new PermissionRequired();
                loggedIn = user;
                PermissionSet.loadPermission(user.getPermissions());
                Main.logger.info("User with user id ["+user.getId()+"] has logged in");
            } else {
                throw new AccessDeniedException();
            }
        } catch (NoResultException e) {
            throw new AccessDeniedException();
        }
    }
}
