package kernbeisser.DBEntities;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
public class SaleSession {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_S_SID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_S_SID_WRITE)})
  private int sSid;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_CUSTOMER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_CUSTOMER_WRITE)})
  private User customer;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_SECOND_SELLER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_SECOND_SELLER_WRITE)})
  private User secondSeller;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_SELLER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_SELLER_WRITE)})
  private User seller;

  public static List<SaleSession> getAll(String condition) {
    return Tools.getAll(SaleSession.class, condition);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SaleSession that = (SaleSession) o;
    return sSid == that.sSid
        && customer.equals(that.customer)
        && secondSeller.equals(that.secondSeller)
        && seller.equals(that.seller);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sSid, customer, secondSeller, seller);
  }
}
