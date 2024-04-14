package kernbeisser.Forms.FormImplemetations.User;

import static kernbeisser.DBConnection.ExpressionFactory.concat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.HashSet;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.ExpressionFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.User_;
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
        new HashSet<>(
            QueryBuilder.select(User_.username)
                .where(User_.firstName.eq(firstName))
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
    return QueryBuilder.propertyWithThatValueExists(User_.username, username);
  }

  boolean fullNameExists(User user) {
    return QueryBuilder.select(User_.id)
        .where(
            concat(
                    concat(User_.firstName, ExpressionFactory.asExpression(" ")),
                    User_.surname)
                .eq(user.getFullName().trim()),
            User_.id.eq(user.getId()).not())
        .hasResult();
  }

  public boolean invalidMembershipRoles(User user) {
    return (user.isFullMember() && user.isTrialMember());
  }
}
