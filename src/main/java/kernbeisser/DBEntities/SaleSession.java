package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.util.List;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Security.Access.UserRelated;
import kernbeisser.Useful.Tools;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class SaleSession implements UserRelated {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_ID_WRITE)})
  private int id;

  @Getter(onMethod_ = {@Key(PermissionKey.SALE_SESSION_ID_READ)})
  private final SaleSessionType sessionType;

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

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.PURCHASE_USER_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PURCHASE_USER_SURCHARGE_WRITE)})
  private Transaction transaction;

  public SaleSession(SaleSessionType sessionType) {
    this.sessionType = sessionType;
  }

  // default constructor just for Hibernate. don't use it
  private SaleSession() {
    this.sessionType = null;
  }

  public static List<SaleSession> getAll(String condition) {
    return Tools.getAll(SaleSession.class, condition);
  }

  public String getSessionTypeName() {
    return sessionType == null ? "UNKNOWN" : getSessionType().name();
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.getUserGroup().equals(customer.getUserGroup());
  }
}
