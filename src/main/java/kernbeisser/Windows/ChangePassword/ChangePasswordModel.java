package kernbeisser.Windows.ChangePassword;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class ChangePasswordModel implements IModel<ChangePasswordController> {

  private final User user;
  private final boolean verifyWithOldPassword;

  public ChangePasswordModel(User user, boolean verifyWithOldPassword) {
    this.user = user;
    this.verifyWithOldPassword = verifyWithOldPassword;
  }

  public boolean checkPassword(String password) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    String currentPassword =
        (String)
            em.createQuery("select u.password from User u where u.id = :id")
                .setParameter("id", user.getId())
                .getSingleResult();
    BCrypt.Result r =
        BCrypt.verifyer().verify(password.toCharArray(), (currentPassword.toCharArray()));
    return r.verified;
  }

  public void changePassword(String newPasswordHash) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    User db = em.find(User.class, user.getId());
    db.setPassword(newPasswordHash);
    em.persist(db);
    em.flush();
  }

  public boolean verifyWithOldPassword() {
    return verifyWithOldPassword;
  }
}
