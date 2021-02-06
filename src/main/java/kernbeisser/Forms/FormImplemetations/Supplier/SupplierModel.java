package kernbeisser.Forms.FormImplemetations.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;

public class SupplierModel implements IModel<SupplierController> {

  public boolean nameExists(String name) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      em.createQuery("select id from Supplier where name like :n")
          .setParameter("n", name)
          .getSingleResult();
      return true;
    } catch (NoResultException e) {
      return false;
    }
  }

  public boolean shortNameExists(String name) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      em.createQuery("select id from Supplier where shortName like :n")
          .setParameter("n", name)
          .getSingleResult();
      return true;
    } catch (NoResultException e) {
      return false;
    }
  }
}
