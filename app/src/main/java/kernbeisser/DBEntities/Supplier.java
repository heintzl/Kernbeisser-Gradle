package kernbeisser.DBEntities;

import static kernbeisser.DBConnection.PredicateFactory.like;
import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Supplier_;
import kernbeisser.DBEntities.SurchargeGroup_;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
@Audited
public class Supplier implements Serializable, ActuallyCloneable {

  @Id
  @Column(updatable = false, insertable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_ID_WRITE)})
  private int id;

  @Column(unique = true)
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_NAME_WRITE)})
  private String name;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_PHONE_NUMBER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_PHONE_NUMBER_WRITE)})
  private String phoneNumber;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_FAX_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_FAX_WRITE)})
  private String fax;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_STREET_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_STREET_WRITE)})
  private String street;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_STREET_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_STREET_WRITE)})
  private String location;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_EMAIL_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_EMAIL_WRITE)})
  private String email;

  @Column(unique = true)
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_SHORT_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_SHORT_NAME_WRITE)})
  private String shortName;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_SURCHARGE_WRITE)})
  private double defaultSurcharge;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_KEEPER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_KEEPER_WRITE)})
  private String keeper;

  @CreationTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_CREATE_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_CREATE_DATE_WRITE)})
  private Instant createDate;

  @UpdateTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.SUPPLIER_UPDATE_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SUPPLIER_UPDATE_DATE_WRITE)})
  private Instant updateDate;

  public SurchargeGroup getOrPersistDefaultSurchargeGroup() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getOrPersistDefaultSurchargeGroup(em);
  }

  public SurchargeGroup getOrPersistDefaultSurchargeGroup(EntityManager em) {
    try {
      return QueryBuilder.selectAll(SurchargeGroup.class)
          .where(
              SurchargeGroup_.parent.isNull(),
              SurchargeGroup_.supplier.eq(this),
              SurchargeGroup_.name.eq(SurchargeGroup.defaultListNameQualifier(this)))
          .getSingleResult();
    } catch (NoResultException e) {
      SurchargeGroup defaultGroup = new SurchargeGroup();
      if (this.shortName.equals("GRE")) {
        defaultGroup.setSupplier(getSupplierByShortName("GR"));
      } else {
        defaultGroup.setSupplier(this);
      }
      defaultGroup.setName(SurchargeGroup.defaultListNameQualifier(this));
      em.persist(defaultGroup);
      em.flush();
      return defaultGroup;
    }
  }

  public static Supplier getSupplierByShortName(String shortName) throws NoResultException {
    return QueryBuilder.selectAll(Supplier.class)
        .where(Supplier_.shortName.eq(shortName))
        .getSingleResult();
  }

  private static Supplier getOrCreateSupplierByShortName(
      String shortName, String defaultName, double defaultSurcharge) {
    try {
      return getSupplierByShortName(shortName);
    } catch (NoResultException e) {
      EntityManager em = DBConnection.getEntityManager();
      EntityTransaction et = em.getTransaction();
      et.begin();
      Supplier s = new Supplier();
      s.setName(defaultName);
      s.setShortName(shortName);
      s.setDefaultSurcharge(defaultSurcharge);
      em.persist(s);
      em.flush();
      et.commit();
      em.close();
      return getSupplierByShortName(shortName);
    }
  }

  public static Supplier getKKSupplier() {
    return getOrCreateSupplierByShortName(
        "KK", "Kornkraft Großhandel", Setting.SURCHARGE_DEFAULT.getDoubleValue());
  }

  public static Supplier getBakerySupplier() {
    return getOrCreateSupplierByShortName(
        "AWB", "Kennung Backwaren", Setting.SURCHARGE_BAKERY.getDoubleValue());
  }

  public static Supplier getProduceSupplier() {
    return getOrCreateSupplierByShortName(
        "AWO", "Kennung Obst&Gemüse", Setting.SURCHARGE_PRODUCE.getDoubleValue());
  }

  public static Supplier getSolidaritySupplier() {
    return getOrCreateSupplierByShortName("SOZ", "Kennung Solidaritätszuschlag", 0);
  }

  public static Supplier getDepositSupplier() {
    return getOrCreateSupplierByShortName("PF", "Kennung Pfand", 0);
  }

  public static Supplier getCustomProductSupplier() {
    return getOrCreateSupplierByShortName("CP", "Kennung Benutzerdefiniert", 0);
  }

  @Override
  public String toString() {
    return Tools.runIfPossible(() -> getName() + "[" + getShortName() + "]")
        .orElse("Lieferant[" + id + "]");
  }

  public static Collection<Supplier> defaultSearch(String s, int max) {
    String containsPattern = "%" + s + "%";
    return QueryBuilder.selectAll(Supplier.class)
        .where(
            or(
                like(Supplier_.name, containsPattern),
                like(Supplier_.keeper, containsPattern),
                like(Supplier_.phoneNumber, containsPattern),
                like(Supplier_.email, containsPattern),
                like(Supplier_.fax, containsPattern)))
        .limit(max)
        .getResultList();
  }

  @Override
  public Shelf clone() throws CloneNotSupportedException {
    try {
      return (Shelf) super.clone();
    } catch (CloneNotSupportedException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }
}
