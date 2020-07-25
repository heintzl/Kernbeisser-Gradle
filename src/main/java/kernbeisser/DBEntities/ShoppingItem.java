package kernbeisser.DBEntities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.transaction.NotSupportedException;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@Getter
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
    this.metricUnits = (isContainerDiscount()?MetricUnits.PIECE:articleBase.getMetricUnits());
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
    this.metricUnits = article.isWeighable() ? article.getMetricUnits() : MetricUnits.PIECE;
    this.weighAble = article.isWeighable();
    this.unitAmount =
        weighAble
                || article.getMetricUnits() == MetricUnits.NONE
                || article.getMetricUnits() == MetricUnits.PIECE
                || !(article.getAmount() > 0)
            ? ""
            : article.getAmount() + article.getMetricUnits().getShortName();
    this.surcharge =
        article.getSurcharge()
            * (hasContainerDiscount ? Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue() : 1);
  }

  public static ShoppingItem createOrganic(double price, boolean hasContainerDiscount) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      ShoppingItem out =
          new ShoppingItem(
              em.createQuery(
                      "select  i from Article i where name like 'Obst und Gem\u00fcse'",
                      Article.class)
                  .getSingleResult(),
              0,
              false) {
            @Override
            public void addToRetailPrice(double addedRetailPrice) {
              ShoppingItem.addToRetailPrice(this, addedRetailPrice);
            }
          };
      out.addToRetailPrice(price);
      out.containerDiscount = hasContainerDiscount;
      return out;
    } catch (NoResultException e) {
      et.begin();
      Article produce = new Article();
      produce.setName("Obst und Gem\u00fcse");
      produce.setKbNumber(-1);
      produce.setMetricUnits(MetricUnits.PIECE);
      produce.setDeleteAllowed(false);
      produce.setVat(VAT.LOW);
      produce.setSurcharge(Setting.SURCHARGE_PRODUCE.getDoubleValue());
      em.persist(produce);
      em.flush();
      et.commit();
      return createOrganic(price, hasContainerDiscount);
    } catch (NotSupportedException e) {
      Tools.showUnexpectedErrorWarning(e);
      return null;
    }
  }

  public static ShoppingItem createBakeryProduct(double price, boolean hasContainerDiscount) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      ShoppingItem out =
          new ShoppingItem(
              em.createQuery("select  i from Article i where name like 'Backwaren'", Article.class)
                  .getSingleResult(),
              0,
              false) {
            @Override
            public void addToRetailPrice(double addedRetailPrice) {
              ShoppingItem.addToRetailPrice(this, addedRetailPrice);
            }
          };
      out.addToRetailPrice(price);
      out.containerDiscount = hasContainerDiscount;
      return out;
    } catch (NoResultException e) {
      et.begin();
      Article bakery = new Article();
      bakery.setName("Backwaren");
      bakery.setKbNumber(-2);
      bakery.setMetricUnits(MetricUnits.PIECE);
      bakery.setDeleteAllowed(false);
      bakery.setVat(VAT.LOW);
      bakery.setSurcharge(Setting.SURCHARGE_BAKERY.getDoubleValue());
      em.persist(bakery);
      em.flush();
      et.commit();
      return createBakeryProduct(price, hasContainerDiscount);
    } catch (NotSupportedException e) {
      Tools.showUnexpectedErrorWarning(e);
      return null;
    }
  }

  public static ShoppingItem createDeposit(double price) {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      ShoppingItem out =
          new ShoppingItem(
              em.createQuery("select  i from Article i where name like 'Pfand'", Article.class)
                  .getSingleResult(),
              0,
              false) {
            @Override
            public void addToRetailPrice(double addedRetailPrice) {
              ShoppingItem.addToRetailPrice(this, addedRetailPrice);
            }
          };
      out.addToRetailPrice(price);
      out.name += (price < 0 ? " zurück" : "");
      return out;
    } catch (NoResultException e) {
      et.begin();
      Article deposit = new Article();
      deposit.setName("Pfand");
      deposit.setKbNumber(-3);
      deposit.setMetricUnits(MetricUnits.PIECE);
      deposit.setDeleteAllowed(false);
      deposit.setVat(VAT.HIGH);
      em.persist(deposit);
      em.flush();
      et.commit();
      return createDeposit(price);
    } catch (NotSupportedException e) {
      Tools.showUnexpectedErrorWarning(e);
      return null;
    }
    // TODO wie wird der Pfand verbucht? Als NetPrice oder irgendwie anders?
  }

  public static List<ShoppingItem> getAll(String condition) {
    return Tools.getAll(ShoppingItem.class, condition);
  }

  private static void addToRetailPrice(ShoppingItem item, double addRetailPrice) {
    item.itemRetailPrice += Math.round(addRetailPrice * 100) / 100.;
    item.itemNetPrice = item.itemRetailPrice / (1 + item.surcharge) / (1 + item.vat);
  }

  public double getRetailPrice() {
    return itemRetailPrice * itemMultiplier * (isContainerDiscount()?1.0: metricUnits.getBaseFactor());
  }

  public double calculateItemRetailPrice(double netPrice) {
    return Math.round(100. * netPrice * (1 + vat) * (1 + surcharge) * (1 - discount / 100.)) / 100.;
  }

  public ShoppingItem createItemDeposit() {
    ShoppingItem deposit = createDeposit(this.singleDeposit);
    deposit.name = "    > Einzelpfand";
    deposit.superIndex = this.getShoppingCartIndex();
    deposit.itemMultiplier = this.itemMultiplier;
    return deposit;
  }

  public ShoppingItem createContainerDeposit(int number) {
    ShoppingItem deposit = createDeposit(this.containerDeposit);
    deposit.name = "    > Gebindepfand";
    deposit.superIndex = this.getShoppingCartIndex();
    deposit.itemMultiplier = number;
    return deposit;
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

  public ShoppingItem newInstance() {
    return new ShoppingItem(this);
  }

  public ShoppingItem(ShoppingItem other) {
    this.amount = other.amount;
    this.discount = other.discount;
    this.purchase = other.purchase;
    this.name = other.name;
    this.kbNumber = other.kbNumber;
    this.itemMultiplier = other.itemMultiplier;
    this.vat = other.vat;
    this.metricUnits = other.metricUnits;
    this.weighAble = other.weighAble;
    this.suppliersItemNumber = other.suppliersItemNumber;
    this.shortName = other.shortName;
    this.surcharge = other.surcharge;
    this.containerDiscount = other.containerDiscount;
    this.itemRetailPrice = other.itemRetailPrice;
    this.itemNetPrice = other.itemNetPrice;
    this.singleDeposit = other.singleDeposit;
    this.containerDeposit = other.containerDeposit;
    this.containerSize = other.containerSize;
    this.shoppingCartIndex = other.shoppingCartIndex;
    this.superIndex = other.superIndex;
  }

  public void addToRetailPrice(double addedRetailPrice) throws NotSupportedException {
    throw new NotSupportedException();
  }

  public MetricUnits getMetricUnits() {
    return metricUnits != null ? metricUnits : MetricUnits.NONE;
  }

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
          && item.vat == vat
          && item.itemRetailPrice == itemRetailPrice
          && item.containerDiscount == containerDiscount
          && item.superIndex == superIndex;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return Tools.decide(this::getName, "Einkaufsartikel[" + siid + "]");
  }
}
