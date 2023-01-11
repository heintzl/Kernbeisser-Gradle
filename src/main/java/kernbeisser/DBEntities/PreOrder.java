package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
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

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_DELIVERY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_DELIVERY_WRITE)})
  private Instant orderedOn;

  public static List<PreOrder> getAll(String condition) {
    return Tools.getAll(PreOrder.class, condition);
  }

  public LocalDate getDueDate() {
    Instant orderDate = orderedOn == null ? Instant.now() : orderedOn;
    return orderDate
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .with(TemporalAdjusters.next(Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class)));
  }

  // required for PreOrderChecklist Report
  public LocalDate getDueLimit() {
    LocalDate dueDate = getDueDate();
    if (dueDate == null) {
      return null;
    }
    return getDueDate().plusDays(Setting.PREORDER_RETARD_THRESHOLD.getIntValue());
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

  public String getContainerInfo() {
    return Articles.getContentAmount(getArticle());
  }

  @Override
  public String toString() {
    return "Container[" + id + "]";
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    if (this.createDate == null) { // must be a new preorder that cannot have a user relation
      return true;
    }
    return this.user.equals(user);
  }

  public boolean isDelivered() {
    return delivery != null;
  }
}
