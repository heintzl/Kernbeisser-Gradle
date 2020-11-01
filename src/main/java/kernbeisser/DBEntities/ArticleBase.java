package kernbeisser.DBEntities;

import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Table
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(doNotUseGetters = true)
public class ArticleBase {
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
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

  public SurchargeTable getSurchargeTable() {
    // TODO really expensive!
    if (supplier == null) return SurchargeTable.DEFAULT;
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery(
              "select st from SurchargeTable st where st.supplier.id = :supplier and st.from_number <= :number and st.to_number >= :number",
              SurchargeTable.class)
          .setParameter("supplier", supplier.getId())
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
      double supplierSurcharge = supplier.getSurcharge();
      if (supplierSurcharge > 0) {
        surcharge = supplierSurcharge;
      }
    }
    return surcharge;
  }

  public static ArticleBase getBySuppliersItemNumber(int suppliersNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
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
    @Cleanup EntityManager em = DBConnection.getEntityManager();
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
}
