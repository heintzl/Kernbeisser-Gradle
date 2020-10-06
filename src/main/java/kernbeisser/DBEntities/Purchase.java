package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class Purchase {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.PURCHASE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PURCHASE_ID_WRITE)})
  private int id;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.PURCHASE_SESSION_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PURCHASE_SESSION_WRITE)})
  private SaleSession session;

  @CreationTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.PURCHASE_CREATE_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PURCHASE_CREATE_DATE_WRITE)})
  private Instant createDate;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.PURCHASE_USER_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PURCHASE_USER_SURCHARGE_WRITE)})
  private double userSurcharge;

  public static List<Purchase> getAll(String condition) {
    return Tools.getAll(Purchase.class, condition);
  }

  public Collection<ShoppingItem> getAllItems() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<ShoppingItem> out =
        em.createQuery(
                "select i from ShoppingItem i where i.purchase.id = " + id, ShoppingItem.class)
            .getResultList();
    em.close();
    return out;
  }

  public double getSum() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em
        .createQuery("select s from ShoppingItem s where purchase.id = :id", ShoppingItem.class)
        .setParameter("id", id).getResultList().stream()
        .mapToDouble(ShoppingItem::getRetailPrice)
        .sum();
  }
}
