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
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
public class SurchargeTable implements Serializable, Cloneable {

  public static final SurchargeTable DEFAULT;

  static {
    SurchargeTable standard = new SurchargeTable();
    standard.from = -1;
    standard.to = -1;
    standard.name = "DEFAULT";
    standard.supplier = null;
    standard.surcharge = 0.07;
    DEFAULT = standard;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_STID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_STID_WRITE)})
  private int stid;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SURCHARGE_WRITE)})
  private double surcharge;

  @Column(name = "\"from\"")
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_FROM_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_FROM_WRITE)})
  private int from;

  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_TO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_TO_WRITE)})
  @Column(name = "\"to\"")
  private int to;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_NAME_WRITE)})
  private String name;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_WRITE)})
  private Supplier supplier;

  public SurchargeTable() {}

  public static List<SurchargeTable> getAll(String condition) {
    return Tools.getAll(SurchargeTable.class, condition);
  }

  public static Collection<SurchargeTable> defaultSearch(String s, int max) {
    EntityManager em = DBConnection.getEntityManager();
    Collection<SurchargeTable> out =
        em.createQuery(
                "select s from SurchargeTable s where s.name like :search or s.supplier.name like :search or s.supplier.shortName like :search",
                SurchargeTable.class)
            .setParameter("search", s + "%")
            .setMaxResults(max)
            .getResultList();
    em.close();
    return out;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SurchargeTable that = (SurchargeTable) o;
    return stid == that.stid
        && Double.compare(that.surcharge, surcharge) == 0
        && from == that.from
        && to == that.to
        && name.equals(that.name)
        && supplier.equals(that.supplier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stid, surcharge, from, to, name, supplier);
  }
}
