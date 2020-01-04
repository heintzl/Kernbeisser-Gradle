package kernbeisser.Windows.CatalogInput;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.ItemKK;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;

class CatalogInputModel implements Model {
    void saveAll(Collection<ItemKK> items){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        items.forEach(em::persist);
        em.flush();
        et.commit();
        em.close();
        //FINISH
    }
    void clearCatalog(){
        EntityManager em = DBConnection.getEntityManager();
        em.createQuery("delete Item");
        em.close();
    }
}
