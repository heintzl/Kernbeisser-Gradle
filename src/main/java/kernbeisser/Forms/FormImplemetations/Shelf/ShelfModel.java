package kernbeisser.Forms.FormImplemetations.Shelf;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class ShelfModel implements IModel<ShelfController> {

  boolean shelfNoExists(int shelfNo) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select s from Shelf s where shelfNo = :s")
            .setParameter("s", shelfNo)
            .getResultList()
            .size()
        > 0;
  }

  boolean locationExists(String location) {

    if (location.isEmpty()) {
      return true;
    }
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select s from Shelf s where location = :l")
            .setParameter("l", location)
            .getResultList()
            .size()
        > 0;
  }
}
