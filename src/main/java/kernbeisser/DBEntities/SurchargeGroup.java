package kernbeisser.DBEntities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class SurchargeGroup implements Serializable, Cloneable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_ID_WRITE)})
  private int id;

  @Column
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SURCHARGE_WRITE)})
  private Double surcharge;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_NAME_WRITE)})
  private String name;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_WRITE)})
  private Supplier supplier;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_WRITE)})
  private SurchargeGroup parent;

  public SurchargeGroup() {}

  public static List<SurchargeGroup> getAll(String condition) {
    return Tools.getAll(SurchargeGroup.class, condition);
  }

  public static Collection<SurchargeGroup> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<SurchargeGroup> out =
        em.createQuery(
                "select s from SurchargeGroup s where s.name like :search or s.supplier.name like :search or s.supplier.shortName like :search",
                SurchargeGroup.class)
            .setParameter("search", s + "%")
            .setMaxResults(max)
            .getResultList();
    em.close();
    return out;
  }

  @Key(PermissionKey.SURCHARGE_TABLE_SURCHARGE_READ)
  public double getSurcharge() {
    if (surcharge == null) {
      return parent == null ? supplier.getDefaultSurcharge() : parent.getSurcharge();
    } else return surcharge;
  }

  public boolean isSurchargeExtracted() {
    return surcharge == null;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SurchargeGroup that = (SurchargeGroup) o;
    return Objects.equals(id, that.id);
  }
}
