package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class Purchase {
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

  /* the following member variables are used to pass non static values to reports */
  @Column @Transient @Getter private String sellerIdentification;

  @Column @Transient @Getter private String customerIdentification;

  public static List<Purchase> getAll(String condition) {
    return Tools.getAll(Purchase.class, condition);
  }

  public Purchase withUserIdentification(boolean withNames) {

    Purchase out = new Purchase();
    out = this;
    User seller = session.getSeller();
    User customer = session.getCustomer();
    out.sellerIdentification = withNames ? seller.getFullName() : Integer.toString(seller.getId());
    out.customerIdentification =
        withNames ? customer.getFullName() : Integer.toString(customer.getId());
    return out;
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

  public double getFilteredSum(Predicate<ShoppingItem> filter) {
    return getAllItems().stream().filter(filter).mapToDouble(ShoppingItem::getRetailPrice).sum();
  }

  public double getSum() {
    return getFilteredSum(s -> true);
  }

  public double guessVatValue(VAT vat) {
    try {
      return getAllItems().stream()
          .filter(si -> si.getVat() == vat)
          .findFirst()
          .get()
          .getVatValue();
    } catch (NoSuchElementException e) {
      return 0.0;
    }
  }
}
