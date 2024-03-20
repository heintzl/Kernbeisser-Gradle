package kernbeisser.Forms.FormImplemetations.User;

import static kernbeisser.DBConnection.ExpressionFactory.concat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.HashSet;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.ExpressionFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.TypeFields.UserField;
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
            QueryBuilder.select(UserField.username)
                .where(UserField.firstName.eq(firstName))
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
    return QueryBuilder.propertyWithThatValueExists(UserField.username, username);
  }

  boolean fullNameExists(User user) {
    return QueryBuilder.select(UserField.id)
        .where(
            concat(
                    concat(UserField.firstName, ExpressionFactory.asExpression(" ")),
                    UserField.surname)
                .eq(user.getFullName().trim()),
            UserField.id.eq(user.getId()).not())
        .hasResult();
  }

  public boolean invalidMembershipRoles(User user) {
    return (user.isFullMember() && user.isTrialMember());
  }
}
