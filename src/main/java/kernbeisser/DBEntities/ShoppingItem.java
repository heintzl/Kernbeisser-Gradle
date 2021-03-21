package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.*;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table
@NoArgsConstructor
// @EqualsAndHashCode(doNotUseGetters = true)
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

  @JoinColumn
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
  @Setter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_VAT_WRITE)})
  private VAT vat;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_VATVALUE_READ)})
  @Setter(
      onMethod_ = {@Key(PermissionKey.SHOPPING_ITEM_VATVALUE_WRITE)},
      value = AccessLevel.PRIVATE)
  private double vatValue;

  @Column
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
  private String suppliersShortName;

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

  @Column @Getter @Setter @CreationTimestamp private Instant createDate;

  @Getter @Transient private double singleDeposit;

  @Getter @Transient private double containerDeposit;

  @Getter @Transient private double containerSize;

  @Getter @Transient private ShoppingItem parentItem;

  @Getter @Transient private Supplier supplier;

  @Getter @Transient private boolean solidaritySurcharge = false;

  @Getter @Transient private boolean specialOffer;
  // required for article labels and pricelists
  @Getter @Transient private String shortBarcode = "";

  @Getter @Transient private String lastDeliveryMonth = "";

  /**
   * @param article most ShoppingItem properties are copied from given article. surcharge gets
   *     calculated
   * @param discount percentage of netprice reduction
   * @param hasContainerDiscount if true reduced surcharge is applied
   */
  public ShoppingItem(Article article, int discount, boolean hasContainerDiscount) {
    this.containerDiscount = hasContainerDiscount;
    this.amount = article.getAmount();
    double offerNetPrice = article.getOfferNetPrice();
    if (offerNetPrice == -999.0) {
      this.itemNetPrice = article.getNetPrice();
      this.specialOffer = false;
    } else {
      this.itemNetPrice = offerNetPrice;
      this.specialOffer = true;
    }
    this.name = (specialOffer ? Setting.OFFER_PREFIX.getStringValue() : "") + article.getName();
    this.metricUnits = article.getMetricUnits();
    this.vat = article.getVat();
    if (this.vat != null) {
      this.vatValue = vat.getValue();
    }
    this.discount = discount;
    supplier = article.getSupplier();
    this.surcharge =
        (supplier == null
                // is unsafe call
                ? Supplier.getKKSupplier().getOrPersistDefaultSurchargeGroup().getSurcharge()
                : article.getSurchargeGroup().getSurcharge())
            * (hasContainerDiscount ? Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue() : 1);
    if (supplier != null) {
      this.suppliersShortName = article.getSupplier().getShortName();
    }
    this.suppliersItemNumber = article.getSuppliersItemNumber();
    this.singleDeposit = article.getSingleDeposit();
    this.containerDeposit = article.getContainerDeposit();
    this.containerSize = article.getContainerSize();
    this.kbNumber = article.getKbNumber();
    this.weighAble = article.isWeighable();
    if (hasContainerDiscount && this.weighAble) {
      this.itemNetPrice *= this.amount * this.metricUnits.getBaseFactor();
    }
    this.itemRetailPrice = calculateItemRetailPrice(itemNetPrice);
  }

  public static ShoppingItem createReportItem(Article article) {
    ShoppingItem item = new ShoppingItem(article, 0, false);
    try {
      String barcode = Long.toString(article.getBarcode());
      item.shortBarcode = barcode.substring(barcode.length() - 4);
    } catch (NullPointerException ignored) {
    }
    try {
      item.lastDeliveryMonth = Date.INSTANT_MONTH_YEAR.format(article.getLastDelivery());
    } catch (NullPointerException ignored) {
    }
    return item;
  }

  public static ShoppingItem createRawPriceProduct(
      String name,
      double price,
      VAT vat,
      int kbNumber,
      Supplier supplier,
      boolean hasContainerDiscount) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
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
      Article article = new Article();
      article.setName(name);
      article.setKbNumber(kbNumber);
      article.setMetricUnits(MetricUnits.NONE);
      article.setVat(vat);
      article.setSupplier(supplier);
      article.setSurchargeGroup(supplier.getOrPersistDefaultSurchargeGroup(em));
      em.persist(article);
      em.flush();
      return createRawPriceProduct(name, price, vat, kbNumber, supplier, hasContainerDiscount);
    }
  }

  public static ShoppingItem createProduce(double price, boolean hasContainerDiscount) {
    return createRawPriceProduct(
        RawPrice.PRODUCE.getName(),
        price,
        VAT.LOW,
        -1,
        Supplier.getProduceSupplier(),
        hasContainerDiscount);
  }

  public static ShoppingItem createBakeryProduct(double price, boolean hasContainerDiscount) {
    return createRawPriceProduct(
        RawPrice.BAKERY.getName(),
        price,
        VAT.LOW,
        -2,
        Supplier.getBakerySupplier(),
        hasContainerDiscount);
  }

  public static ShoppingItem createSolidaritySurcharge(double price, VAT vat, double surcharge) {
    ShoppingItem solidarity =
        createRawPriceProduct(
            RawPrice.SOLIDARITY.getName(), price, vat, -4, Supplier.getSolidaritySupplier(), false);
    solidarity.solidaritySurcharge = true;
    solidarity.name =
        (int) (surcharge * 100)
            + " % "
            + RawPrice.SOLIDARITY.getName()
            + " MWSt. "
            + (vat == VAT.HIGH ? "voll" : "ermäßigt");
    solidarity.vat = vat;
    solidarity.vatValue = vat.getValue();
    return solidarity;
  }

  public static ShoppingItem createDeposit(double price) {
    ShoppingItem deposit =
        createRawPriceProduct(
            RawPrice.DEPOSIT.getName(), price, VAT.HIGH, -3, Supplier.getDepositSupplier(), false);
    deposit.name += price < 0 ? " zurück" : "";
    return deposit;
  }

  public static List<ShoppingItem> getAll(String condition) {
    return Tools.getAll(ShoppingItem.class, condition);
  }

  @Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)
  public double getRetailPrice() {
    return Math.round(
            100.0
                * itemRetailPrice
                * itemMultiplier
                * (!weighAble ? 1.0 : getSalesUnits().getBaseFactor()))
        / 100.0;
  }

  public MetricUnits getSalesUnits() {
    if (isContainerDiscount()) {
      return MetricUnits.CONTAINER;
    }
    return getContainerUnits();
  }

  public MetricUnits getContainerUnits() {
    if (!isWeighAble() && getMetricUnits() != MetricUnits.NONE) {
      return MetricUnits.PIECE;
    }
    return getMetricUnits();
  }

  public MetricUnits getPriceUnits() {
    MetricUnits priceUnits = getMetricUnits();
    switch (priceUnits) {
      case GRAM:
        priceUnits = MetricUnits.KILOGRAM;
        break;
      case MILLILITER:
        priceUnits = MetricUnits.LITER;
        break;
      default:
    }
    if ((isContainerDiscount() || !isWeighAble()) && getMetricUnits() != MetricUnits.NONE) {
      priceUnits = MetricUnits.PIECE;
    }
    return priceUnits;
  }

  public String getDisplayAmount() {

    if (isContainerDiscount() && !isWeighAble() && getContainerSize() != 0.0) {
      return Math.round(getItemMultiplier() / getContainerSize())
          + " "
          + MetricUnits.CONTAINER.getShortName();
    }
    return getPriceUnits() == MetricUnits.NONE
        ? ""
        : getItemMultiplier() + " " + getSalesUnits().getShortName();
  }

  public String getContentAmount() {
    if (isWeighAble()
        || this.getMetricUnits() == MetricUnits.NONE
        || this.getMetricUnits() == MetricUnits.PIECE
        || !(this.getAmount() > 0)) {
      return "";
    } else {
      return this.getAmount() + " " + this.getMetricUnits().getShortName();
    }
  }

  @Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)
  public double calculatePreciseRetailPrice(double netPrice) {
    return netPrice * (1 + vatValue) * (1 + surcharge) * (1 - discount / 100.);
  }

  @Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)
  public double calculateItemRetailPrice(double netPrice) {
    return Tools.roundCurrency(calculatePreciseRetailPrice(netPrice));
  }

  public ShoppingItem createItemDeposit(int number, boolean isContainer) {
    double itemDeposit = isContainer ? containerDeposit : singleDeposit;
    ShoppingItem deposit = createDeposit(itemDeposit);
    deposit.name = (isContainer ? RawPrice.CONTAINER_DEPOSIT : RawPrice.ITEM_DEPOSIT).getName();
    deposit.metricUnits = MetricUnits.PIECE;
    deposit.setItemRetailPrice(itemDeposit);
    deposit.setItemNetPrice(itemDeposit / deposit.calculatePreciseRetailPrice(1.0));
    deposit.parentItem = this;
    deposit.itemMultiplier = number;
    return deposit;
  }

  public ShoppingItem createSingleDeposit(int multiplier) {
    return createItemDeposit(multiplier, false);
  }

  public ShoppingItem createContainerDeposit(int number) {
    return createItemDeposit(number, true);
  }

  public Article extractArticleBySupplierNumber() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select a from Article a where suppliersItemNumber = :sn and supplier = :s",
            Article.class)
        .setParameter("sn", suppliersItemNumber)
        .setParameter("s", supplier)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public Article extractArticle() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("SELECT i from Article i where kbNumber = " + kbNumber, Article.class)
        .getResultStream()
        .findAny()
        .orElse(null);
  }

  public static double getSum(ShoppingItemSum sumType, Collection<ShoppingItem> items) {
    return getSum(sumType, items, s -> true);
  }

  public static double getSum(
      ShoppingItemSum sumType, Collection<ShoppingItem> items, Predicate<ShoppingItem> filter) {
    Predicate<ShoppingItem> typeFilter;
    switch (sumType) {
      case RETAILPRICE_TOTAL:
      case VAT_TOTAL:
        typeFilter = s -> true;
        break;
      case RETAILPRICE_VATLOW:
      case VAT_VATLOW:
        typeFilter = s -> s.vat == VAT.LOW;
        break;
      case RETAILPRICE_VATHIGH:
      case VAT_VATHIGH:
        typeFilter = s -> s.vat == VAT.HIGH;
        break;
      default:
        typeFilter = s -> false;
    }

    ToDoubleFunction<ShoppingItem> argument;
    switch (sumType) {
      case RETAILPRICE_TOTAL:
      case RETAILPRICE_VATLOW:
      case RETAILPRICE_VATHIGH:
        argument = ShoppingItem::getRetailPrice;
        break;
      case VAT_TOTAL:
      case VAT_VATLOW:
      case VAT_VATHIGH:
        argument = s -> s.getRetailPrice() * (1 - 1 / (1 + s.getVatValue()));
        break;
      default:
        argument = s -> 0.0;
    }

    return items.stream().filter(filter).filter(typeFilter).mapToDouble(argument).sum();
  }

  public ShoppingItem unproxy() {
    ShoppingItem newInstance = new ShoppingItem();
    Tools.copyInto(this, newInstance);
    return newInstance;
  }

  @Key(PermissionKey.SHOPPING_ITEM_METRIC_UNITS_READ)
  public MetricUnits getMetricUnits() {
    return metricUnits != null ? metricUnits : MetricUnits.NONE;
  }

  @Key(PermissionKey.SHOPPING_ITEM_SHORT_NAME_READ)
  public String getSafeSuppliersShortName() {
    return (suppliersShortName == null ? "" : suppliersShortName);
  }
  // TODO maybe replace this with check at ShoppingCartTable to not bind the hash code to this class
  @Override
  public int hashCode() {
    return kbNumber * ((discount % 100) + 1) * amount;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ShoppingItem) {
      ShoppingItem item = (ShoppingItem) obj;
      return item.discount == discount
          && item.name.equals(name)
          && item.kbNumber == kbNumber
          && item.vatValue == vatValue
          && item.itemRetailPrice == itemRetailPrice
          && item.containerDiscount == containerDiscount
          && item.parentItem == parentItem;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return Tools.decide(this::getName, "Einkaufsartikel[" + id + "]");
  }
}
