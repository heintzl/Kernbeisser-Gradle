package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.Key;
import kernbeisser.Security.Relations.UserRelated;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class PreOrder implements Serializable, UserRelated {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_ID_WRITE)})
  private int id;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_ITEM_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_ITEM_WRITE)})
  private Article article;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_USER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_USER_WRITE)})
  private User user;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_INFO_WRITE)})
  private String info;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_AMOUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_AMOUNT_WRITE)})
  private int amount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_DELIVERY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_DELIVERY_WRITE)})
  private Instant delivery;

  @CreationTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_CREATE_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_CREATE_DATE_WRITE)})
  private Instant createDate;

  public static List<PreOrder> getAll(String condition) {
    return Tools.getAll(PreOrder.class, condition);
  }

  // required for PreOrderChecklist Report
  public boolean isRetarded() {
    return ChronoUnit.DAYS.between(createDate, Instant.now())
        > Setting.PREORDERRETARD_THRESHOLD.getIntValue();
  }

  public int getKBNumber() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select a.kbNumber from Article a where  suppliersItemNumber = :i and supplier = :s",
            Integer.class)
        .setParameter("i", article.getSuppliersItemNumber())
        .setParameter("s", article.getSupplier())
        .getResultStream()
        .findFirst()
        .orElse(-1);
  }

  public boolean isShopOrder() {
    return user.equals(User.getKernbeisserUser());
  }

  @Override
  public String toString() {
    return "Container[" + id + "]";
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return this.user.equals(user);
  }
}
