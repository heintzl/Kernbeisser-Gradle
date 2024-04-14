package kernbeisser.Windows.Supply;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.PreOrder_;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import kernbeisser.Windows.Supply.SupplySelector.LineContent;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
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

  public Optional<Article> findBySuppliersItemNumber(Supplier supplier, int suppliersItemNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return ArticleRepository.getBySuppliersItemNumber(supplier, suppliersItemNumber, em);
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
      log.warn(
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
    return ArticleRepository.getBySuppliersItemNumber(selected, suppliersItemNumber)
        .orElseThrow(NoSuchElementException::new);
  }

  public static Map<CatalogEntry, Integer> getUserPreorderEntryCount() {
    List<PreOrder> userPreorders =
        DBConnection.getConditioned(
            PreOrder.class,
            PreOrder_.delivery.isNull(),
            PreOrder_.user.eq(User.getKernbeisserUser()).not());
    Map<CatalogEntry, Integer> entryCounts = new HashMap<>(userPreorders.size());
    for (PreOrder preorder : userPreorders) {
      CatalogEntry entry = preorder.getCatalogEntry();
      Integer countEntry = entryCounts.getOrDefault(entry, 0);
      entryCounts.put(entry, countEntry + preorder.getAmount());
    }
    return entryCounts;
  }

  public static int getLabelCount(
      int suppliersItemNumber,
      double priceKk,
      Article article,
      double containerMultiplier,
      int preOrders) {
    if (article == null) return 0;
    if (article.getSupplier().equals(Supplier.getKKSupplier()) && suppliersItemNumber < 1000)
      return 0;
    if (priceKk == 0.0) return 0;
    int containersForShop;
    if (article.isWeighable()) {
      containersForShop =
          Math.round((float) containerMultiplier / (float) article.getContainerSize()) - preOrders;
    } else {
      containersForShop = (int) Math.ceil(containerMultiplier) - preOrders;
    }
    if (containersForShop <= 0) return 0;
    return article.isLabelPerUnit()
        ? article.getLabelCount() + containersForShop
        : article.getLabelCount();
  }

  public static int getPrintNumberFromLineContent(LineContent content) {
    return getLabelCount(
        content.getKkNumber(),
        content.getPriceKk(),
        content.getArticle(),
        content.getContainerMultiplier(),
        content.getUserPreorderCount());
  }

  public int calculatePrintNumberFromItem(ShoppingItem item) {
    Article article = item.getArticleAtBuyState();
    int preorders = 0;
    if (article.getSupplier().equals(Supplier.getKKSupplier())) {
      preorders = getPreorderCount(article.getSuppliersItemNumber());
    }
    return getLabelCount(
        item.getSuppliersItemNumber(),
        item.getItemNetPrice(),
        article,
        -item.getContainerCount(),
        preorders);
  }

  public int getPreorderCount(int suppliersItemNumber) {
    return kkNumberPreorderCounts.getOrDefault(suppliersItemNumber, 0);
  }

  public void addShoppingItem(ShoppingItem item) {
    int existingItemIndex = shoppingItems.indexOf(item);
    if (existingItemIndex != -1) {
      ShoppingItem articleBefore = shoppingItems.get(existingItemIndex);
      item.setItemMultiplier(item.getItemMultiplier() + articleBefore.getItemMultiplier());
      shoppingItems.remove(existingItemIndex);
    }
    shoppingItems.addFirst(item);
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

  public List<Supplier> getAllSuppliers() {
    return Tools.getAll(Supplier.class);
  }
}
