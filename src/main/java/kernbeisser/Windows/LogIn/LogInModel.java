package kernbeisser.Windows.LogIn;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.PermissionRequired;
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
            } else {
                throw new AccessDeniedException();
            }
        } catch (NoResultException e) {
            throw new AccessDeniedException();
        }
    }

    public Collection<User> getAllUserWitchBeginsWith(char c) {
        return User.getAll("where username like '" + c + "%' Order by username asc");
    }

    public Collection<User> getAllUser() {
        return User.getAll("Order by username asc");
    }
}
