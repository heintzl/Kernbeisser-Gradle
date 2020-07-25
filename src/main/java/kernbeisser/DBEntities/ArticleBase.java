package kernbeisser.DBEntities;

import java.util.Objects;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ArticleBase {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_NAME_WRITE)})
  private String name;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_PRODUCER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_PRODUCER_WRITE)})
  private String producer;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_NET_PRICE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_NET_PRICE_WRITE)})
  private double netPrice;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_METRIC_UNITS_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_METRIC_UNITS_WRITE)})
  private MetricUnits metricUnits;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_SUPPLIER_WRITE)})
  private Supplier supplier;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_WRITE)})
  private int suppliersItemNumber;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_VAT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_VAT_WRITE)})
  private VAT vat;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_AMOUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_AMOUNT_WRITE)})
  private int amount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_BARCODE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_BARCODE_WRITE)})
  private Long barcode;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_SIZE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_SIZE_WRITE)})
  private double containerSize;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_SINGLE_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_SINGLE_DEPOSIT_WRITE)})
  private double singleDeposit;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_DEPOSIT_WRITE)})
  private double containerDeposit;

  public void setData(ArticleBase other) {
    this.name = other.name;
    this.producer = other.producer;
    this.netPrice = other.netPrice;
    this.metricUnits = other.metricUnits;
    this.supplier = other.supplier;
    this.suppliersItemNumber = other.suppliersItemNumber;
    this.vat = other.vat;
    this.amount = other.amount;
    this.barcode = other.barcode;
    this.containerSize = other.containerSize;
    this.singleDeposit = other.singleDeposit;
    this.containerDeposit = other.containerDeposit;
  }

  public SurchargeTable getSurchargeTable() {
    // TODO really expensive!
    if (supplier == null) return SurchargeTable.DEFAULT;
    EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery(
              "select st from SurchargeTable st where st.supplier.id = :supplier and st.from <= :number and st.to >= :number",
              SurchargeTable.class)
          .setParameter("supplier", supplier.getSid())
          .setParameter("number", suppliersItemNumber)
          .setMaxResults(1)
          .getSingleResult();
    } catch (NoResultException e) {
      return SurchargeTable.DEFAULT;
    }
  }

  public double calculateSurcharge() {
    SurchargeTable surchargeTable = getSurchargeTable();
    double surcharge = surchargeTable.getSurcharge();

    if (surchargeTable == SurchargeTable.DEFAULT && supplier != null) {
      double supplierSurcharge = supplier.getSurcharge() / 100.0;
      if (supplierSurcharge > 0) {
        surcharge = supplierSurcharge;
      }
    }

    return surcharge;
  }

  public static ArticleBase getBySuppliersItemNumber(int suppliersNumber) {
    EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery(
              "select i from ArticleBase i where suppliersItemNumber = :n", ArticleBase.class)
          .setParameter("n", suppliersNumber)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    } finally {
      em.close();
    }
  }

  public static ArticleBase getByBarcode(long barcode) {
    EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select i from Article i where barcode = :n", ArticleBase.class)
          .setParameter("n", barcode)
          .getSingleResult();
    } catch (NoResultException f) {
      return null;
    } finally {
      em.close();
    }
  }

  @Override
  public String toString() {
    return Tools.decide(this::getName, "ArtikelBase[" + id + "]");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArticleBase that = (ArticleBase) o;
    return id == that.id
        && Double.compare(that.netPrice, netPrice) == 0
        && suppliersItemNumber == that.suppliersItemNumber
        && amount == that.amount
        && Double.compare(that.containerSize, containerSize) == 0
        && Double.compare(that.singleDeposit, singleDeposit) == 0
        && Double.compare(that.containerDeposit, containerDeposit) == 0
        && name.equals(that.name)
        && producer.equals(that.producer)
        && metricUnits == that.metricUnits
        && supplier.equals(that.supplier)
        && vat == that.vat
        && barcode.equals(that.barcode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        name,
        producer,
        netPrice,
        metricUnits,
        supplier,
        suppliersItemNumber,
        vat,
        amount,
        barcode,
        containerSize,
        singleDeposit,
        containerDeposit);
  }
}
