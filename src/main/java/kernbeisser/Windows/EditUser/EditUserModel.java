package kernbeisser.Windows.EditUser;

import java.util.HashSet;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MVC.IModel;

public class EditUserModel implements IModel<EditUserController> {

  private final User user;
  private final Mode mode;

  public EditUserModel(User user, Mode mode) {
    this.user = user;
    this.mode = mode;
  }

  String generateUsername(String firstName, String surname) {
    EntityManager em = DBConnection.getEntityManager();
    @SuppressWarnings("unchecked")
    HashSet<String> usernames =
        new HashSet<String>(
            em.createQuery("select u.username from User u where firstName = :firstName")
                .setParameter("firstName", firstName)
                .getResultList());
    for (int i = 1; i < surname.length() + 1; i++) {
      String generated = firstName + "." + surname.substring(0, i);
      if (!usernames.contains(generated)) {
        return generated;
      }
    }
    try {
      return firstName + "." + surname.substring(0, 1) + "" + usernames.size();
    } catch (IndexOutOfBoundsException e) {
      return firstName + "." + usernames.size();
    }
  }

  boolean usernameExists(String username) {
    EntityManager em = DBConnection.getEntityManager();
    boolean exists =
        em.createQuery("select id from User where username like :username")
                .setParameter("username", username)
                .getResultList()
                .size()
            > 0;
    em.close();
    return exists;
  }

  public User getUser() {
    return user;
  }

  public Mode getMode() {
    return mode;
  }
}
