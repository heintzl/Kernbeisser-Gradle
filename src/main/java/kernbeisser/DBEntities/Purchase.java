package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Security.Relations.UserRelated;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;
import rs.groump.PermissionKey;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class Purchase implements UserRelated {
  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  @Getter(onMethod_ = {@Key(PermissionKey.PURCHASE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PURCHASE_ID_WRITE)})
  private long id;

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

  /* the following member variables are used to pass non-static values to reports */
  @Column @Transient @Getter private String sellerIdentification;

  @Column @Transient @Getter private String customerIdentification;

  public static List<Purchase> getAll(String condition) {
    return Tools.getAll(Purchase.class, condition);
  }

  public Purchase withUserIdentification(boolean withNames) {

    Purchase out = this;
    User seller = session.getSeller();
    User customer = session.getCustomer();
    out.sellerIdentification = withNames ? seller.getFullName() : Integer.toString(seller.getId());
    out.customerIdentification =
        withNames ? customer.getFullName() : Integer.toString(customer.getId());
    return out;
  }

  public Collection<ShoppingItem> getAllItems() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select i from ShoppingItem i where i.purchase.id = " + id, ShoppingItem.class)
        .getResultList();
  }

  public double getFilteredSum(Predicate<ShoppingItem> filter) {
    return getAllItems().stream().filter(filter).mapToDouble(ShoppingItem::getRetailPrice).sum();
  }

  public double getSum() {
    return getFilteredSum(s -> true);
  }

  public double guessVatValue(VAT vat) {
    return getAllItems().stream()
        .filter(si -> si.getVat() == vat)
        .findFirst()
        .map(ShoppingItem::getVatValue)
        .orElse(0.0);
  }

  public static long getLastBonNo() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Optional.ofNullable(
            em.createQuery("select max(id) from Purchase p", Long.class).getSingleResult())
        .orElse(-1L);
  }

  public static Optional<Purchase> getLastBon() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Optional.ofNullable(
        em.createQuery("select p from Purchase p order by p.id desc", Purchase.class)
            .setMaxResults(1)
            .getSingleResult());
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return session == null
        || user.getUserGroup().getId() == session.getCustomer().getUserGroup().getId();
  }
}
