package kernbeisser.DBEntities;

import static kernbeisser.DBConnection.PredicateFactory.like;
import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import kernbeisser.CustomComponents.ObjectTree.CachedNode;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.TypeFields.ArticleField;
import kernbeisser.DBEntities.TypeFields.SupplierField;
import kernbeisser.DBEntities.TypeFields.SurchargeGroupField;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.ActuallyCloneable;
import lombok.*;
import org.hibernate.envers.Audited;
import rs.groump.Key;
import rs.groump.PermissionKey;

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

  @Column @Setter() private Double surcharge;

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

  public static Collection<SurchargeGroup> defaultSearch(String s, int max) {
    String searchPattern = s + "%";
    return QueryBuilder.selectAll(SurchargeGroup.class)
        .where(
            or(
                like(SurchargeGroupField.name, searchPattern),
                like(SurchargeGroupField.supplier.child(SupplierField.name), searchPattern),
                like(SurchargeGroupField.supplier.child(SupplierField.shortName), searchPattern)))
        .limit(max)
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
    return QueryBuilder.selectAll(SurchargeGroup.class)
        .where(SurchargeGroupField.parent.eq(this))
        .getResultList();
  }

  public static Node<SurchargeGroup> allSurchargeGroupsFromSupplierAsNode(Supplier s) {
    SurchargeGroup head =
        new SurchargeGroup() {
          @Override
          public List<SurchargeGroup> getSubGroups() {
            return QueryBuilder.selectAll(SurchargeGroup.class)
                .where(SurchargeGroupField.supplier.eq(s), SurchargeGroupField.parent.isNull())
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
    return QueryBuilder.selectAll(Article.class)
        .where(ArticleField.surchargeGroup.eq(this))
        .getResultList();
  }

  @Override
  public SurchargeGroup clone() throws CloneNotSupportedException {
    try {
      return (SurchargeGroup) super.clone();
    } catch (CloneNotSupportedException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }
}
