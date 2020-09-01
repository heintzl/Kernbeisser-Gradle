package kernbeisser.Windows.EditSupplier;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MVC.IModel;

public class EditSupplierModel implements IModel<EditSupplierController> {
  private final Supplier supplier;
  private final Mode mode;

  public EditSupplierModel(Supplier supplier, Mode mode) {
    this.supplier = supplier;
    this.mode = mode;
  }

  public Supplier getSupplier() {
    return supplier;
  }

  public Mode getMode() {
    return mode;
  }

  public boolean nameExists(String name) {
    EntityManager em = DBConnection.getEntityManager();
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
    EntityManager em = DBConnection.getEntityManager();
    try {
      em.createQuery("select id from Supplier where shortName like :n")
          .setParameter("n", name)
          .getSingleResult();
      return true;
    } catch (NoResultException e) {
      return false;
    }
  }

  public void hasUsage(Supplier supplier) {}
}
