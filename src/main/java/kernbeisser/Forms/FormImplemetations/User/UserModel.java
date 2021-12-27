package kernbeisser.Forms.FormImplemetations.User;

import java.util.HashSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class UserModel implements IModel<UserController> {

  public UserModel() {}

  String generateUsername(String firstName, String surname) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
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
      return firstName + "." + surname.charAt(0) + "" + usernames.size();
    } catch (IndexOutOfBoundsException e) {
      return firstName + "." + usernames.size();
    }
  }

  boolean usernameExists(String username) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select id from User where username like :username")
            .setParameter("username", username)
            .getResultList()
            .size()
        > 0;
  }

  boolean fullNameExists(User user) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
                "select id from User where id <> :currentId and concat(firstName, ' ', surname) = :fullName")
            .setParameter("fullName", user.getFullName().trim())
            .setParameter("currentId", user.getId())
            .getResultList()
            .size()
        > 0;
  }

  public boolean invalidMembershipRoles(User user) {
    return (user.isFullMember() && user.isTrialMember());
  }
}
