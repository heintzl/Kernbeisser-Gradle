package kernbeisser.Windows.ChangePassword;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

public class ChangePasswordModel implements Model<ChangePasswordController> {

  private final User user;
  private final boolean verifyWithOldPassword;

  public ChangePasswordModel(User user, boolean verifyWithOldPassword) {
    this.user = user;
    this.verifyWithOldPassword = verifyWithOldPassword;
  }

  public boolean checkPassword(String password) {
    EntityManager em = DBConnection.getEntityManager();
    String currentPassword =
        (String)
            em.createQuery("select u.password from User u where u.id = :uid")
                .setParameter("uid", user.getId())
                .getSingleResult();
    em.close();
    BCrypt.Result r =
        BCrypt.verifyer().verify(password.toCharArray(), (currentPassword.toCharArray()));
    return r.verified;
  }

  public void changePassword(String newPasswordHash) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    User db = em.find(User.class,user.getId());
    db.setPassword(newPasswordHash);
    em.persist(db);
    em.flush();
    et.commit();
    em.close();
  }

  public boolean verifyWithOldPassword() {
    return verifyWithOldPassword;
  }
}
