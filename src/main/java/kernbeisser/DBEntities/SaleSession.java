package kernbeisser.DBEntities;

import java.util.List;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class SaleSession {
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
}
