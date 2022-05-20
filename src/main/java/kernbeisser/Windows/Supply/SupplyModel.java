package kernbeisser.Windows.Supply;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

public class SupplyModel implements IModel<SupplyController> {

  @Getter @Setter private double appendedProducePrice = 0;
  private final Map<Article, Integer> print = new HashMap<>();
  private final Map<Integer, Integer> printPoolBefore = new HashMap<>();
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

  public Optional<Article> findBySuppliersItemNumber(Supplier supplier, int suppliersItemNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Articles.getBySuppliersItemNumber(supplier, suppliersItemNumber, em);
  }

  void print() {
    Articles.addToPrintPool(print, printPoolBefore);
    print.clear();
  }

  public void setPrintNumber(Article article, Integer number) {
    print.put(article, number);
    printPoolBefore.putIfAbsent(article.getId(), article.getPrintPool());
  }

  public int getPrintNumber(Article article) {
    return Optional.ofNullable(print.get(article)).orElse(0);
  }

  public boolean isPrintSelected() {
    return !print.isEmpty();
  }

  public boolean articleExists(Supplier supplier, int suppliersItemNumber) {
    return findBySuppliersItemNumber(supplier, suppliersItemNumber).isPresent();
  }

  public Article getBySuppliersItemNumber(Supplier selected, int suppliersItemNumber) {
    return Articles.getBySuppliersItemNumber(selected, suppliersItemNumber)
        .orElseThrow(NoSuchElementException::new);
  }
}
