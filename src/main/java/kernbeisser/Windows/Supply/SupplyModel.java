package kernbeisser.Windows.Supply;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.FieldCondition;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.Supply.SupplySelector.LineContent;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

public class SupplyModel implements IModel<SupplyController> {

  @Getter @Setter private double appendedProducePrice = 0;
  private final Map<ShoppingItem, Integer> print = new HashMap<>();
  @Setter private Map<Article, Integer> printPoolBefore = new HashMap<>();
  private final List<ShoppingItem> shoppingItems = new ArrayList<>();
  private final Map<Integer, Integer> kkNumberPreorderCounts = new HashMap<>();

  public SupplyModel() {
    for (Map.Entry<CatalogEntry, Integer> entry : getUserPreorderEntryCount().entrySet()) {
      kkNumberPreorderCounts.put(entry.getKey().getArtikelNrInt(), entry.getValue());
    }
  }

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

  public static Map<CatalogEntry, Integer> getUserPreorderEntryCount() {
    List<PreOrder> userPreorders =
        DBConnection.getConditioned(
            PreOrder.class,
            new FieldCondition("delivery", null),
            new FieldCondition("user", User.getKernbeisserUser()).not());
    Map<CatalogEntry, Integer> entryCounts = new HashMap<>();
    for (PreOrder preorder : userPreorders) {
      CatalogEntry entry = preorder.getCatalogEntry();
      entryCounts.putIfAbsent(entry, 0);
      entryCounts.replace(entry, entryCounts.get(entry), preorder.getAmount());
    }
    return entryCounts;
  }

  private static int getUserPreorderCount(Article article) {
    if (!article.getSupplier().equals(Supplier.getKKSupplier())) {
      return 0;
    }
    List<CatalogEntry> articleCatalogEntries =
        CatalogEntry.getByArticleNo(Integer.toString(article.getSuppliersItemNumber()));
    if (articleCatalogEntries.isEmpty()) {
      return 0;
    }
    FieldCondition[] conditions = {
      new FieldCondition("delivery", null),
      new FieldCondition("user", User.getKernbeisserUser()).not(),
      new FieldCondition("catalogEntry", articleCatalogEntries)
    };
    return DBConnection.getConditioned(PreOrder.class, conditions).stream()
        .mapToInt(PreOrder::getAmount)
        .sum();
  }

  public static Integer getLabelCount(
      int kkNumber, double priceKk, Article article, double containerMultiplier, int preOrders) {
    if (kkNumber < 1000) return 0;
    if (priceKk == 0.0) return 0;
    if (article == null) return 0;
    float containersForShop = (float) containerMultiplier - preOrders;
    if (article.isLabelPerUnit()) {
      if (article.isWeighable()) {
        return Math.round(containersForShop / (float) article.getContainerSize());
      }
      return (int) Math.ceil(containersForShop);
    }
    if (containersForShop > 0.0) return article.getLabelCount();
    return 0;
  }

  public static Integer getPrintNumberFromLineContent(LineContent content) {
    return getLabelCount(
        content.getKkNumber(),
        content.getPriceKk(),
        content.getArticle(),
        content.getContainerMultiplier(),
        content.getUserPreorderCount());
  }

  public Integer getPrintNumberFromItem(ShoppingItem item) {
    Article article = item.getArticleAtBuyState();
    int preorders = 0;
    if (article.getSupplier().equals(Supplier.getKKSupplier())) {
      preorders = Tools.ifNull(kkNumberPreorderCounts.get(article.getSuppliersItemNumber()),0);
    }
    return getLabelCount(
        (item.getSupplier().equals(Supplier.getKKSupplier())
            ? item.getSuppliersItemNumber()
            : 100000),
        item.getItemNetPrice(),
        article,
        -item.getContainerCount(),
        preorders);
  }

  public void addShoppingItem(ShoppingItem item) {
    int existingItemIndex = shoppingItems.indexOf(item);
    if (existingItemIndex != -1) {
      ShoppingItem articleBefore = shoppingItems.get(existingItemIndex);
      item.setItemMultiplier(item.getItemMultiplier() + articleBefore.getItemMultiplier());
      shoppingItems.remove(existingItemIndex);
    }
    shoppingItems.add(0, item);
  }

  public List<ShoppingItem> getShoppingItems() {
    return Collections.unmodifiableList(shoppingItems);
  }

  public void clearShoppingItems() {
    shoppingItems.clear();
  }

  public void removeShoppingItem(ShoppingItem item) {
    shoppingItems.remove(item);
  }
}
