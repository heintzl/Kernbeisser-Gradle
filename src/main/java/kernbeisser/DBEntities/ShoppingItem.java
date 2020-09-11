package kernbeisser.DBEntities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import javax.transaction.NotSupportedException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.RawPrice;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import lombok.*;

@Entity
@Table
@NoArgsConstructor
@Getter
@EqualsAndHashCode(doNotUseGetters = true)
public class ShoppingItem implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long siid;

  @Column private int amount;

  @Column private int discount;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Setter
  private Purchase purchase;

  @Column @Setter private String name;

  @Column private int kbNumber;

  @Column @Setter private int itemMultiplier = 1;

  @Column private double vat;

  @Column
  @Getter(AccessLevel.NONE)
  private MetricUnits metricUnits;

  @Column private String unitAmount;

  @Column private boolean weighAble;

  @Column private int suppliersItemNumber;

  @Column(length = 5)
  private String shortName;

  @Column private double surcharge;

  @Column private boolean containerDiscount;

  @Column @Setter private double itemRetailPrice;

  @Column @Setter private double itemNetPrice;

  @Column @Setter private int shoppingCartIndex;

  @Transient private double singleDeposit;

  @Transient private double containerDeposit;

  @Transient private double containerSize;

  @Transient private int superIndex;

  @Transient private Supplier supplier;

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
    this.unitAmount =
        articleBase.getMetricUnits() == MetricUnits.NONE
                || articleBase.getMetricUnits() == MetricUnits.PIECE
                || !(articleBase.getAmount() > 0)
            ? ""
            : articleBase.getAmount() + articleBase.getMetricUnits().getShortName();
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
    this.unitAmount =
        this.weighAble
                || article.getMetricUnits() == MetricUnits.NONE
                || article.getMetricUnits() == MetricUnits.PIECE
                || !(article.getAmount() > 0)
            ? ""
            : article.getAmount() + article.getMetricUnits().getShortName();
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

  public double getRetailPrice() {
    return itemRetailPrice
        * itemMultiplier
        * (isContainerDiscount() || !weighAble ? 1.0 : metricUnits.getBaseFactor());
  }

  public double calculatePreciseRetailPrice(double netPrice) {
    return netPrice * (1 + vat) * (1 + surcharge) * (1 - discount / 100.);
  }

  public double calculateItemRetailPrice(double netPrice) {
    return Math.round(100. * calculatePreciseRetailPrice(netPrice)) / 100.;
  }

  public ShoppingItem createItemDeposit(int number, boolean isContainer) {
    double itemDeposit = isContainer ? containerDeposit : singleDeposit;
    ShoppingItem deposit = createDeposit(itemDeposit);
    deposit.name = (isContainer ? RawPrice.CONTAINERDEPOSIT : RawPrice.ITEMDEPOSIT).getName();
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
    return Tools.decide(this::getName, "Einkaufsartikel[" + siid + "]");
  }
}
