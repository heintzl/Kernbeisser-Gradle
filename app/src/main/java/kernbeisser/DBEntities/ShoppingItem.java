package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.EntityWrapper.ObjectState;
import kernbeisser.Enums.*;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.envers.AuditReaderFactory;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table(
    indexes = {
      @Index(name = "IX_item_name", columnList = "name"),
      @Index(name = "IX_item_kbNumber", columnList = "kbNumber"),
      @Index(name = "IX_item_suppliersItemNumber", columnList = "suppliersItemNumber"),
      @Index(name = "IX_item_Article", columnList = "articleId")
    })
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

  @Column private long articleId;

  @Column private int articleRev;

  @Column(nullable = false)
  private Instant createDate;

  @Getter @Transient private double singleDeposit;

  @Getter @Transient private double containerDeposit;

  @Getter @Transient private double containerSize;

  @Getter @Transient private ShoppingItem parentItem;

  @Getter @Transient private Supplier supplier;

  @Getter @Transient private boolean solidaritySurchargeItem = false;

  @Getter @Transient private boolean depositItem = false;

  @Getter @Transient private boolean specialOffer;

  @PrePersist
  private void setTimestamp() {
    createDate = Instant.now();
  }

  /**
   * @param discount percentage of netprice reduction
   * @param hasContainerDiscount if true reduced surcharge is applied
   */
  public ShoppingItem(Article article, int discount, boolean hasContainerDiscount) {
    this(ObjectState.currentState(article), discount, hasContainerDiscount);
  }

  /**
   * @param article most ShoppingItem properties are copied from given article. surcharge gets
   *     calculated
   * @param discount percentage of netprice reduction
   * @param hasContainerDiscount if true reduced surcharge is applied
   */
  public ShoppingItem(ObjectState<Article> article, int discount, boolean hasContainerDiscount) {
    this(article.getValue(), article.getRevNumber(), discount, hasContainerDiscount);
  }

  public static String getOfferPrefix() {
    return Setting.OFFER_PREFIX.getStringValue();
  }

  /**
   * @param article most ShoppingItem properties are copied from given article. surcharge gets
   *     calculated
   * @param discount percentage of netprice reduction
   * @param hasContainerDiscount if true reduced surcharge is applied
   */
  public ShoppingItem(Article article, int articleRev, int discount, boolean hasContainerDiscount) {
    this.containerDiscount = hasContainerDiscount;
    this.amount = Articles.getSafeAmount(article);
    this.specialOffer = article.isOffer();
    this.name = (specialOffer ? getOfferPrefix() : "") + article.getName();
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
            * (hasContainerDiscount ? Articles.getContainerSurchargeReduction() : 1);
    if (supplier != null) {
      this.suppliersShortName = article.getSupplier().getShortName();
    }
    this.suppliersItemNumber = article.getSuppliersItemNumber();
    this.singleDeposit = article.getSingleDeposit();
    this.containerDeposit = article.getContainerDeposit();
    this.containerSize = article.getContainerSize();
    this.kbNumber = article.getKbNumber();
    this.weighAble = article.isWeighable();
    this.itemNetPrice = Articles.calculateUnroundedArticleNetPrice(article, hasContainerDiscount);
    setItemRetailPriceFromNetPrice();
    this.articleId = article.getId();
    this.articleRev = articleRev;
  }

  public static ShoppingItem createRawPriceProduct(
      RawPrice rawPrice, double price, boolean hasContainerDiscount) {
    ShoppingItem out =
        new ShoppingItem(
            ObjectState.currentState(Articles.getOrCreateRawPriceArticle(rawPrice)),
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
  }

  public static ShoppingItem createProduce(double price, boolean hasContainerDiscount) {
    return createRawPriceProduct(RawPrice.PRODUCE, price, hasContainerDiscount);
  }

  public static ShoppingItem createBakeryProduct(double price, boolean hasContainerDiscount) {
    return createRawPriceProduct(RawPrice.BAKERY, price, hasContainerDiscount);
  }

  public static ShoppingItem createSolidaritySurcharge(double price, VAT vat, double surcharge) {
    ShoppingItem solidarity = createRawPriceProduct(RawPrice.SOLIDARITY, price, false);
    solidarity.solidaritySurchargeItem = true;
    solidarity.vatValue = vat.getValue();
    solidarity.name =
        (int) (surcharge * 100)
            + "% "
            + RawPrice.SOLIDARITY.getName()
            + " auf Artikel mit "
            + Math.round(solidarity.vatValue * 100)
            + "% MWSt.";
    solidarity.vat = vat;
    return solidarity;
  }

  public boolean isSolidaritySurcharged() {
    return !depositItem && !solidaritySurchargeItem;
  }

  public static ShoppingItem createDeposit(double price) {
    ShoppingItem deposit = createRawPriceProduct(RawPrice.DEPOSIT, price, false);
    deposit.name += price < 0 ? " zurück" : "";
    deposit.depositItem = true;
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

  public Double getContainerCount() {
    return isWeighAble()
        ? (getMetricUnits().inUnit(getMetricUnits().getDisplayUnit(), itemMultiplier))
        : itemMultiplier / containerSize;
  }

  public String getDisplayContainerCount() {
    double amount =
        isWeighAble()
            ? (getMetricUnits().inUnit(MetricUnits.KILOGRAM, itemMultiplier))
            : itemMultiplier / containerSize;
    String unit =
        isWeighAble()
            ? getMetricUnits() == MetricUnits.MILLILITER
                ? MetricUnits.LITER.getShortName()
                : MetricUnits.KILOGRAM.getShortName()
            : " Geb.";
    return String.format("%s%s", roundIfNecessary(-amount), unit);
  }

  public String roundIfNecessary(double d) {
    return d == Math.round(d) ? String.valueOf(Math.round(d)) : String.format("%.2f", d);
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
  public double calculatePreciseRetailPrice(double netPrice) throws NullPointerException {
    return Articles.calculateRetailPrice(netPrice, vat, surcharge, discount, false);
  }

  @Key(PermissionKey.SHOPPING_ITEM_ITEM_RETAIL_PRICE_READ)
  public void setItemRetailPriceFromNetPrice() {
    setItemRetailPrice(Tools.roundCurrency(calculatePreciseRetailPrice(itemNetPrice)));
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

  public Optional<Article> getArticleNow() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Optional.ofNullable(em.find(Article.class, (int) articleId));
  }

  public Article getArticleAtBuyState() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Article article = AuditReaderFactory.get(em).find(Article.class, (int) articleId, articleRev);
    article.getSurchargeGroup().getSurcharge();
    return article;
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
    return Tools.runIfPossible(this::getName).orElse("Einkaufsartikel[" + id + "]");
  }

  public static ShoppingItem displayOnlyShoppingItem(
      Article article, int discount, boolean hasContainerDiscount) {
    return new ShoppingItem(article, 0, discount, hasContainerDiscount) {
      // prevents hibernate from persisting the Object because it's an instance of the lambda
      // factory
    };
  }
}
