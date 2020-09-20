package kernbeisser.DBEntities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import javax.transaction.NotSupportedException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.*;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;

@Entity
@Table
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
public class ShoppingItem implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ID_WRITE)})
  private long id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_AMOUNT_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_AMOUNT_WRITE)},
      value = AccessLevel.PRIVATE)
  private int amount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_DISCOUNT_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_DISCOUNT_WRITE)},
      value = AccessLevel.PRIVATE)
  private int discount;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_PURCHASE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_PURCHASE_WRITE)})
  private Purchase purchase;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_NAME_WRITE)})
  private String name;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_KB_NUMBER_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_KB_NUMBER_WRITE)},
      value = AccessLevel.PRIVATE)
  private int kbNumber;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ITEM_MULTIPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ITEM_MULTIPLIER_WRITE)})
  private int itemMultiplier = 1;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_VAT_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_VAT_WRITE)},
      value = AccessLevel.PRIVATE)
  private double vat;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_METRIC_UNITS_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_METRIC_UNITS_WRITE)},
      value = AccessLevel.PRIVATE)
  private MetricUnits metricUnits;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_WEIGH_ABLE_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_WEIGH_ABLE_WRITE)},
      value = AccessLevel.PRIVATE)
  private boolean weighAble;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SUPPLIERS_ITEM_NUMBER_WRITE)},
      value = AccessLevel.PRIVATE)
  private int suppliersItemNumber;

  @Column(length = 5)
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SHORT_NAME_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SHORT_NAME_WRITE)},
      value = AccessLevel.PRIVATE)
  private String shortName;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SURCHARGE_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SURCHARGE_WRITE)},
      value = AccessLevel.PRIVATE)
  private double surcharge;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_CONTAINER_DEPOSIT_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_CONTAINER_DEPOSIT_WRITE)},
      value = AccessLevel.PRIVATE)
  private boolean containerDiscount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_WRITE)})
  private double itemRetailPrice;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ITEM_NET_PRICE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_ITEM_NET_PRICE_WRITE)})
  private double itemNetPrice;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SHOPPING_CART_INDEX_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_SHOPPING_CART_INDEX_WRITE)})
  private int shoppingCartIndex;

  @Getter @Transient private double singleDeposit;

  @Getter @Transient private double containerDeposit;

  @Getter @Transient private double containerSize;

  @Getter @Transient private int superIndex;

  @Getter @Transient private Supplier supplier;

  /**
   * @param articleBase most ShoppingItem properties are copied from given article. surcharge gets
   *     calculated
   * @param discount percentage of netprice reduction
   * @param hasContainerDiscount if true reduced surcharge is applied
   */
  public ShoppingItem(ArticleBase articleBase, int discount, boolean hasContainerDiscount) {
    this.containerDiscount = hasContainerDiscount;
    this.name = articleBase.getName();
    this.amount = articleBase.getAmount();
    this.itemNetPrice = articleBase.getNetPrice();
    this.metricUnits =
        (isContainerDiscount() && articleBase.getMetricUnits() != MetricUnits.NONE
            ? MetricUnits.PIECE
            : articleBase.getMetricUnits());
    VAT vat = articleBase.getVat();
    if (vat != null) {
      this.vat = vat.getValue();
    }
    this.surcharge =
        articleBase.calculateSurcharge()
            * (hasContainerDiscount ? Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue() : 1);
    this.discount = discount;
    supplier = articleBase.getSupplier();
    if (supplier != null) {
      this.shortName = articleBase.getSupplier().getShortName();
    }
    this.suppliersItemNumber = articleBase.getSuppliersItemNumber();
    this.singleDeposit = articleBase.getSingleDeposit();
    this.containerDeposit = articleBase.getContainerDeposit();
    this.containerSize = articleBase.getContainerSize();
    this.itemRetailPrice = calculateItemRetailPrice(itemNetPrice);
  }

  /**
   * @param article Article constructor provides some more fields than ArticleBase. surcharge is
   *     taken directly from article
   * @param discount percentage of netprice reduction
   * @param hasContainerDiscount if true reduced surcharge is applied
   */
  public ShoppingItem(Article article, int discount, boolean hasContainerDiscount) {
    this((ArticleBase) article, discount, hasContainerDiscount);
    this.kbNumber = article.getKbNumber();
    this.weighAble = article.isWeighable();
    if (!this.weighAble && this.metricUnits != MetricUnits.NONE) {
      this.metricUnits = MetricUnits.PIECE;
    }
    this.surcharge =
        article.getSurcharge()
            * (hasContainerDiscount ? Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue() : 1);
  }

  public static ShoppingItem createRawPriceProduct(
      String name,
      double price,
      VAT vat,
      int kbNumber,
      double surcharge,
      boolean hasContainerDiscount) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      ShoppingItem out =
          new ShoppingItem(
              em.createQuery("select  i from Article i where name = :n", Article.class)
                  .setParameter("n", name)
                  .getSingleResult(),
              0,
              hasContainerDiscount);
      if (hasContainerDiscount) {
        out.setItemNetPrice(0.01);
        out.setItemRetailPrice(0.01 * out.calculatePreciseRetailPrice(1.0));
      } else {
        out.setItemRetailPrice(0.01);
        out.setItemNetPrice(0.01 / out.calculatePreciseRetailPrice(1.0));
      }
      out.setItemMultiplier((int) Math.round(price * 100.0));
      return out;
    } catch (NoResultException e) {
      et.begin();
      Article article = new Article();
      article.setName(name);
      article.setKbNumber(kbNumber);
      article.setMetricUnits(MetricUnits.NONE);
      article.setDeleteAllowed(false);
      article.setVat(vat);
      article.setSurcharge(surcharge);
      em.persist(article);
      em.flush();
      et.commit();
      return createRawPriceProduct(name, price, vat, kbNumber, surcharge, hasContainerDiscount);
    }
  }

  public static ShoppingItem createProduce(double price, boolean hasContainerDiscount) {
    return createRawPriceProduct(
        RawPrice.PRODUCE.getName(),
        price,
        VAT.LOW,
        -1,
        Setting.SURCHARGE_PRODUCE.getDoubleValue(),
        hasContainerDiscount);
  }

  public static ShoppingItem createBakeryProduct(double price, boolean hasContainerDiscount) {
    return createRawPriceProduct(
        RawPrice.BAKERY.getName(),
        price,
        VAT.LOW,
        -2,
        Setting.SURCHARGE_BAKERY.getDoubleValue(),
        hasContainerDiscount);
  }

  public static ShoppingItem createDeposit(double price) {
    ShoppingItem deposit =
        createRawPriceProduct(RawPrice.DEPOSIT.getName(), price, VAT.HIGH, -3, 0, false);
    deposit.name += price < 0 ? " zurück" : "";
    return deposit;
  }

  public static List<ShoppingItem> getAll(String condition) {
    return Tools.getAll(ShoppingItem.class, condition);
  }

  /*private static void addToRetailPrice(ShoppingItem item, double addRetailPrice) {
    item.itemRetailPrice += Math.round(addRetailPrice * 100) / 100.;
    item.itemNetPrice = item.itemRetailPrice / (1 + item.surcharge) / (1 + item.vat);
  }*/

  @Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)
  public double getRetailPrice() {
    return itemRetailPrice
        * itemMultiplier
        * (isContainerDiscount() || !weighAble ? 1.0 : metricUnits.getBaseFactor());
  }

  public String getUnitAmount() {
    if (this.getMetricUnits() == MetricUnits.NONE
        || this.getMetricUnits() == MetricUnits.PIECE
        || !(this.getAmount() > 0)) {
      return "";
    } else {
      return this.getAmount() + this.getMetricUnits().getShortName();
    }
  }

  @Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)
  public double calculatePreciseRetailPrice(double netPrice) {
    return netPrice * (1 + vat) * (1 + surcharge) * (1 - discount / 100.);
  }

  @Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)
  public double calculateItemRetailPrice(double netPrice) {
    return Math.round(100. * calculatePreciseRetailPrice(netPrice)) / 100.;
  }

  public ShoppingItem createItemDeposit(int number, boolean isContainer) {
    double itemDeposit = isContainer ? containerDeposit : singleDeposit;
    ShoppingItem deposit = createDeposit(itemDeposit);
    deposit.name = (isContainer ? RawPrice.CONTAINER_DEPOSIT : RawPrice.ITEM_DEPOSIT).getName();
    deposit.metricUnits = MetricUnits.PIECE;
    deposit.setItemRetailPrice(itemDeposit);
    deposit.setItemNetPrice(itemDeposit / deposit.calculatePreciseRetailPrice(1.0));
    deposit.superIndex = this.getShoppingCartIndex();
    deposit.itemMultiplier = number;
    return deposit;
  }

  public ShoppingItem createSingleDeposit() {
    return createItemDeposit(this.itemMultiplier, false);
  }

  public ShoppingItem createContainerDeposit(int number) {
    return createItemDeposit(number, true);
  }

  public Article extractArticle() {
    EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("SELECT i from Article i where kbNumber = " + kbNumber, Article.class)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    } finally {
      em.close();
    }
  }

  public static double[] getSums(Collection<ShoppingItem> items) {
    double sum = 0;
    double vatLowSum = 0;
    double vatLowFactor = (1 - 1 / (1 + VAT.LOW.getValue()));
    double vatHighFactor = (1 - 1 / (1 + VAT.HIGH.getValue()));
    double vatHighSum = 0;
    for (ShoppingItem item : items) {
      double retailPrice = item.getRetailPrice();
      sum += retailPrice;
      if (item.getVat() == VAT.LOW.getValue()) vatLowSum += retailPrice * vatLowFactor;
      if (item.getVat() == VAT.HIGH.getValue()) vatHighSum += retailPrice * vatHighFactor;
    }
    return new double[] {sum, vatLowSum, vatHighSum};
  }

  public ShoppingItem unproxy() {
    ShoppingItem newInstance = new ShoppingItem();
    Tools.copyInto(this, newInstance);
    return newInstance;
  }

  public void addToRetailPrice(double addedRetailPrice) throws NotSupportedException {
    throw new NotSupportedException();
  }

  public MetricUnits getMetricUnits() {
    return metricUnits != null ? metricUnits : MetricUnits.NONE;
  }

  // TODO maybe replace this with check at ShoppingCartTable to not bind the hash code to this class
  @Override
  public int hashCode() {
    return kbNumber * ((discount % 100) + 1) * amount;
  }

  @Override
  public String toString() {
    return Tools.decide(this::getName, "Einkaufsartikel[" + id + "]");
  }
}
