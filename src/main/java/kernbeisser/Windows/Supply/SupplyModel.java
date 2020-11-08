package kernbeisser.Windows.Supply;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ArticleBase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class SupplyModel implements IModel<SupplyController> {

  @Getter private final Collection<ShoppingItem> shoppingItems = new ArrayList<>();

  ShoppingItem getViaSuppliersItemNumber(Supplier supplier, int suppliersItemNumber)
      throws NoResultException {
    try {
      @Cleanup EntityManager em = DBConnection.getEntityManager();
      return new ShoppingItem(
          em.createQuery(
                  "select a from ArticleBase a where supplier.id = :sid and suppliersItemNumber = :sin",
                  ArticleBase.class)
              .setParameter("sid", supplier.getId())
              .setParameter("sin", suppliersItemNumber)
              .getResultList()
              .get(0),
          0,
          false);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new NoResultException("cannot find Article via suppliers number");
    }
  }

  void commit() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (ShoppingItem item : shoppingItems) {
      item.setItemMultiplier(-item.getItemMultiplier());
      em.persist(item);
    }
    em.flush();
    et.commit();
  }

  Collection<Supplier> getAllSuppliers() {
    return Tools.getAll(Supplier.class, null);
  }
}
