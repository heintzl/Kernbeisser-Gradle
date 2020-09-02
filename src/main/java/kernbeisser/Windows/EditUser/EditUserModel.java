package kernbeisser.Windows.EditUser;

import java.util.HashSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Useful.Tools;
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

  private void remove(User user) {
    User.makeUserUnreadable(user);
  }

  private void edit(User user) {
    Tools.edit(user.getId(), user);
  }

  private void add(User user) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    UserGroup newUserGroup = new UserGroup();
    em.persist(newUserGroup);
    user.setUserGroup(newUserGroup);
    em.persist(Tools.setId(new User(user), 0));
    em.flush();
    et.commit();
    em.close();
  }

  public User getUser() {
    return user;
  }

  public Mode getMode() {
    return mode;
  }
}
