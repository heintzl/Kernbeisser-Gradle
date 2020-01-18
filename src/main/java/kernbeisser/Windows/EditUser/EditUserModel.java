package kernbeisser.Windows.EditUser;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class EditUserModel implements Model {

    private final User user;
    private final Mode mode;

    public EditUserModel(User user, Mode mode) {
        this.user = user;
        this.mode = mode;
    }

    void doAction(User user){
        switch (mode){
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
    }

    private void remove(User user) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.remove(em.find(User.class,user.getId()));
        em.flush();
        et.commit();
        em.close();
    }

    private void edit(User user) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(Tools.mergeWithoutId(user,em.find(User.class,user.getId())));
        em.flush();
        et.commit();
        em.close();
    }

    private void add(User user) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(Tools.mergeWithoutId(user));
        em.flush();
        et.commit();
        em.close();
    }

    public User getUser() {
        return user;
    }
}
