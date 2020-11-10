package kernbeisser.Windows.Supply;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
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

  Collection<ArticleBase> getViaSuppliersItemNumber(Supplier supplier, int suppliersItemNumber)
      throws NoResultException, NonUniqueResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return getArticleBaseViaSuppliersItemNumber(supplier, suppliersItemNumber, em).getResultList();
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

  private TypedQuery<ArticleBase> getArticleBaseViaSuppliersItemNumber(
      Supplier supplier, int suppliersItemNumber, EntityManager entityManager)
      throws NoResultException {
    return entityManager
        .createQuery(
            "select a from ArticleBase a where supplier.id = :sid and suppliersItemNumber = :sin",
            ArticleBase.class)
        .setParameter("sid", supplier.getId())
        .setParameter("sin", suppliersItemNumber);
  }
}
