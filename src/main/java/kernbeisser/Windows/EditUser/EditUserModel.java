package kernbeisser.Windows.EditUser;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.util.Collection;

public class EditUserModel implements Model {

    private final User user;
    private final Mode mode;

    public EditUserModel(User user, Mode mode) {
        this.user = user;
        this.mode = mode;
        if (mode == Mode.ADD) {
            user.setPassword(null);
        }
    }

    Collection<Permission> getAllPermission() {
        return Permission.getAll(null);
    }

    boolean doAction(User user) {
        try {
            switch (mode) {
                case ADD:
                    add(user);
                    break;
                case EDIT:
                    edit(user);
                    break;
                case REMOVE:
                    remove(user);
                    break;
            }
            return true;
        } catch (PersistenceException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean usernameExists(String username) {
        EntityManager em = DBConnection.getEntityManager();
        boolean exists = em.createQuery("select id from User where username like :username")
                           .setParameter("username", username)
                           .getResultList()
                           .size() > 0;
        em.close();
        return exists;
    }

    private void remove(User user) {
        Tools.delete(User.class, user.getId());
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
        em.persist(Tools.mergeWithoutId(user));
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
