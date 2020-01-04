package kernbeisser.Windows.LogIn;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;

import static kernbeisser.Windows.LogIn.LogInController.*;

public class LogInModel implements Model {

    public static User loggedIn;

    public static User getLoggedIn() {
        return loggedIn;
    }
    LogInModel(){
        loggedIn=null;
    }

    int logIn(String username, char[] password) {
        EntityManager em = DBConnection.getEntityManager();
        try{
            User user = em.createQuery(
                    "select u from User u where u.username like :username", User.class)
                    .setParameter("username", username).
                            getSingleResult();
            if(BCrypt.verifyer().verify(password,user.getPassword().toCharArray()).verified){
                loggedIn=user;
                return SUCCESS;
            }else {
                return INCORRECT_PASSWORD;
            }
        }catch (NoResultException e){
            return INCORRECT_USERNAME;
        }
    }

    Collection<User> getAllUserWitchBeginsWith(char c){
        return User.getAll("where username like '"+c+"%' Order by username asc");
    }

    Collection<User> getAllUser(){
        return User.getAll("Order by username asc");
    }
}
