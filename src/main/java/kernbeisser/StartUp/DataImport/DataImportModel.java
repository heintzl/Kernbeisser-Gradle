package kernbeisser.StartUp.DataImport;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.User;
import kernbeisser.DBEntitys.UserGroup;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

class DataImportModel implements Model {
    <T> void saveAll(Collection<T> v){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        int c = 0;
        for (T t : v) {
            em.persist(t);
            c++;
            if(c % 20 == 0){
                em.close();
                em.clear();
            }
        }
        em.flush();
        et.commit();
        em.close();
    }
    void saveAllUsers(Collection<User> users){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        int c = 0;
        for (User u : users) {
            em.persist(u.getUserGroup());
            em.persist(u);
            c++;
            if(c % 10 == 0){
                em.close();
                em.clear();
            }
        }
        em.flush();
        et.commit();
        em.close();
    }
}
