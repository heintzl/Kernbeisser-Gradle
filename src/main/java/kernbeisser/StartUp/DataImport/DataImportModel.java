package kernbeisser.StartUp.DataImport;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

class DataImportModel implements Model {
    <T> void batchSaveAll(Collection<T> v) {
        if (v.size() == 0) {
            return;
        }
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        int c = 0;
        for (T t : v) {
            em.persist(t);
            c++;
            if (c % 20 == 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
        et.commit();
        em.close();
    }

    <T> void saveAll(Collection<T> v) {
        if (v.size() == 0) {
            return;
        }
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        for (T t : v) {
            em.persist(t);
        }
        em.flush();
        et.commit();
        em.close();
    }

    void saveUser(User first, User second, UserGroup userGroup) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(userGroup);
        first.setUserGroup(userGroup);
        em.persist(first);
        if (second != null) {
            second.setUserGroup(userGroup);
            em.persist(second);
        }
        em.flush();
        et.commit();
    }

    void saveWithPermission(User user, Permission permission) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(permission);
        user.getPermissions().add(permission);
        UserGroup userGroup = new UserGroup();
        em.persist(userGroup);
        user.setUserGroup(userGroup);
        em.persist(user);
        em.flush();
        et.commit();
        em.close();
    }
}
