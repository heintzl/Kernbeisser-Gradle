package kernbeisser.StartUp.DataImport;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.User;
import kernbeisser.DBEntitys.UserGroup;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

class DataImportModel implements Model {
    <T> void batchSaveAll(Collection<T> v){
        if(v.size()==0)return;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        int c = 0;
        for (T t : v) {
            em.persist(t);
            c++;
            if(c % 20 == 0){
                em.flush();
                em.clear();
            }
        }
        em.flush();
        et.commit();
        em.close();
    }
    <T> void saveAll(Collection<T> v){
        if(v.size()==0)return;
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
    void saveUser(User first,User second,UserGroup userGroup){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(userGroup);
        first.setUserGroup(userGroup);
        em.persist(first);
        if(second!=null){
            second.setUserGroup(userGroup);
            em.persist(second);
        }
        em.flush();
        et.commit();
    }
}
