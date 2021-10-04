package kernbeisser.Windows.Supply;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class SupplyModel implements IModel<SupplyController> {

  private final Set<Article> print = new HashSet<>();
  @Getter private final Collection<ShoppingItem> shoppingItems = new ArrayList<>();

  void commit() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (ShoppingItem item : shoppingItems) {
      item.setItemMultiplier(-Math.abs(item.getItemMultiplier()));
      em.persist(item);
    }
    em.flush();
  }

  Collection<Supplier> getAllSuppliers() {
    return Tools.getAll(Supplier.class, null);
  }

  private TypedQuery<Article> getArticleViaSuppliersItemNumber(
      Supplier supplier, int suppliersItemNumber, EntityManager entityManager)
      throws NoResultException {
    return entityManager
        .createQuery(
            "select a from Article a where supplier.id = :sid and suppliersItemNumber = :sin",
            Article.class)
        .setParameter("sid", supplier.getId())
        .setParameter("sin", suppliersItemNumber);
  }

  public Optional<Article> findBySuppliersItemNumber(Supplier supplier, int suppliersItemNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Articles.getBySuppliersItemNumber(supplier, suppliersItemNumber, em);
  }

  public Collection<PriceList> getAllPriceLists() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select p from PriceList p", PriceList.class).getResultList();
  }

  void print() {
    Articles.addToPrintPool(print);
    print.clear();
  }

  public void togglePrint(Article bases) {
    if (!print.remove(bases)) print.add(bases);
  }

  public void setBecomePrinted(Article becomePrinted, boolean value) {
    if (becomePrinted(becomePrinted) != value) togglePrint(becomePrinted);
  }

  public boolean becomePrinted(Article article) {
    return print.contains(article);
  }

  public boolean isPrintSelected() {
    return !print.isEmpty();
  }

  public boolean articleExists(int suppliersItemNumber) {
    try {
      findBySuppliersItemNumber(Supplier.getKKSupplier(), suppliersItemNumber);
      return true;
    } catch (NoResultException e) {
      return false;
    }
  }
}
