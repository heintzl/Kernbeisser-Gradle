package kernbeisser.DBEntities;

import java.time.Instant;
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
import org.hibernate.annotations.CreationTimestamp;

@Table
@Entity
public class Purchase {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.PURCHASE_SID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PURCHASE_SID_WRITE)})
  private int sid;

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
    EntityManager em = DBConnection.getEntityManager();
    Collection<ShoppingItem> out =
        em.createQuery(
                "select i from ShoppingItem i where i.purchase.id = " + sid, ShoppingItem.class)
            .getResultList();
    em.close();
    return out;
  }

  public double getSum() {
    EntityManager em = DBConnection.getEntityManager();
    double sum =
        em.createQuery("select s from ShoppingItem s where purchase.id = :id", ShoppingItem.class)
            .setParameter("id", sid).getResultList().stream()
            .mapToDouble(ShoppingItem::getItemRetailPrice)
            .sum();
    em.close();
    return sum * (1 + userSurcharge);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Purchase purchase = (Purchase) o;
    return sid == purchase.sid
        && Double.compare(purchase.userSurcharge, userSurcharge) == 0
        && session.equals(purchase.session)
        && createDate.equals(purchase.createDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sid, session, createDate, userSurcharge);
  }
}
