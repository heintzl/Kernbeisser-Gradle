package kernbeisser.Windows.ChangePassword;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class ChangePasswordModel implements Model<ChangePasswordController> {

    private final User user;

    public ChangePasswordModel(User user) {
        this.user = user;
    }

    public boolean checkPassword(String password){
        EntityManager em = DBConnection.getEntityManager();
        String currentPassword = (String) em.createQuery("select u.password from User u where u.id = :uid").setParameter("uid", user.getId()).getSingleResult();
        em.close();
        BCrypt.Result r = BCrypt.verifyer().verify(password.toCharArray(),(currentPassword.toCharArray()));
        return r.verified;
    }

    public void changePassword(String newPasswordHash) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.createQuery("update User user set user.password = :password where user.id = :uid")
          .setParameter("password",newPasswordHash)
          .setParameter("uid",user.getId())
          .executeUpdate();
        em.flush();
        et.commit();
        em.close();
    }
}
