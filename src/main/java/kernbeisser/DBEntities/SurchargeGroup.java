package kernbeisser.DBEntities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import kernbeisser.CustomComponents.ObjectTree.CachedNode;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.Key;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.envers.Audited;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
@Audited
public class SurchargeGroup implements Serializable, ActuallyCloneable {

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
  @ManyToOne(fetch = FetchType.EAGER)
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_WRITE)})
  private SurchargeGroup parent;

  public SurchargeGroup() {}

  public static List<SurchargeGroup> getAll(String condition) {
    return Tools.getAll(SurchargeGroup.class, condition);
  }

  public static Collection<SurchargeGroup> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select s from SurchargeGroup s where s.name like :search or s.supplier.name like :search or s.supplier.shortName like :search",
            SurchargeGroup.class)
        .setParameter("search", s + "%")
        .setMaxResults(max)
        .getResultList();
  }

  @Key(PermissionKey.SURCHARGE_TABLE_SURCHARGE_READ)
  public double getSurcharge() {
    if (surcharge == null) {
      return parent == null
          ? supplier == null
              ? Setting.SURCHARGE_DEFAULT.getDoubleValue()
              : supplier.getDefaultSurcharge()
          : parent.getSurcharge();
    } else return surcharge;
  }

  public String getNameWithSurcharge() {
    return getName() + String.format(" (%.0f%%)", getSurcharge() * 100);
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

  public String pathString() {
    if (parent == null) return name;
    return parent.pathString() + ":" + name;
  }

  public List<SurchargeGroup> getSubGroups() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select s from SurchargeGroup s where s.parent.id = :id", SurchargeGroup.class)
        .setParameter("id", this.id)
        .getResultList();
  }

  public static Node<SurchargeGroup> asMappedNode(Supplier s) {
    SurchargeGroup head =
        new SurchargeGroup() {
          @Override
          public List<SurchargeGroup> getSubGroups() {
            @Cleanup EntityManager em = DBConnection.getEntityManager();
            @Cleanup(value = "commit")
            EntityTransaction et = em.getTransaction();
            et.begin();
            return em.createQuery(
                    "select s from SurchargeGroup s where supplier.id = :sid and parent = NULL",
                    SurchargeGroup.class)
                .setParameter("sid", s.getId())
                .getResultList();
          }
        };
    head.setName(s.getName());
    return new CachedNode<>(null, head, SurchargeGroup::getSubGroups);
  }

  public static String defaultListNameQualifier(Supplier s) {
    return "@" + s.getName() + " Standard Aufschlag";
  }

  public Collection<Article> getArticles() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select a from Article a where a.surchargeGroup.id = :sg", Article.class)
        .setParameter("sg", getId())
        .getResultList();
  }

  @Override
  public SurchargeGroup clone() throws CloneNotSupportedException {
    try {
      return (SurchargeGroup) super.clone();
    } catch (CloneNotSupportedException e) {
      throw Tools.showUnexpectedErrorWarning(e);
    }
  }
}
