package kernbeisser.Windows.Supply;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

public class SupplyModel implements IModel<SupplyController> {

  @Getter @Setter private double appendedProducePrice = 0;
  private final Map<ShoppingItem, Integer> print = new HashMap<>();
  @Setter private Map<Article, Integer> printPoolBefore = new HashMap<>();
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
    Map<Article, Integer> newPrintPool = new HashMap<>();
    print.forEach((k, v) -> newPrintPool.merge(k.getArticleNow().get(), v, Integer::sum));
    printPoolBefore.forEach(
        (k, v) -> {
          newPrintPool.merge(k, v, Integer::sum);
        });
    ArticlePrintPool.setPrintPoolFromMap(newPrintPool);
    print.clear();
  }

  public void setContainerMultiplier(ShoppingItem item, double amount) {
    double rawItemMultiplier =
        (item.isWeighAble()
                ? item.getMetricUnits().inUnit(MetricUnits.GRAM, item.getAmount() * amount)
                : amount * item.getContainerSize())
            * -1;
    checkFractionalItemMultiplier(rawItemMultiplier, item.getSuppliersItemNumber());
    item.setItemMultiplier((int) Math.round(rawItemMultiplier));
  }

  public static void checkFractionalItemMultiplier(double itemMultiplier, int suppliersItemNumber) {
    if (itemMultiplier % 1 != 0) {
      Main.logger.warn(
          String.format(
              "fractional item multiplier while reading KKSupplierFile content Article[%s] itemmultiplier: [%f]",
              suppliersItemNumber, itemMultiplier));
    }
  }

  public void setPrintNumber(ShoppingItem item, Integer number) {
    print.put(item, number);
  }

  public int getPrintNumber(ShoppingItem item) {
    return Optional.ofNullable(print.get(item)).orElse(0);
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

  public static Integer getPrintNumberFromItem(ShoppingItem item) {
    if (item.getSuppliersItemNumber() < 1000 && item.getSupplier().equals(Supplier.getKKSupplier()))
      return 0;
    int number = -(int) Math.round(item.getContainerCount());
    if (item.isWeighAble() || number < 1) return 1;
    if (number > 10) return 10;
    return number;
  }
}
