package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import kernbeisser.Enums.PreOrderCreator;
import kernbeisser.Enums.Setting;
import kernbeisser.Security.Access.UserRelated;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table(
    indexes = {
      @Index(name = "IX_preorder_delivery", columnList = "delivery"),
    })
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
  private CatalogEntry catalogEntry;

  // deprecated, do not use after migration
  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_ITEM_READ)})
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

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_ALTERNATIVE_ITEM_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_ALTERNATIVE_ITEM_WRITE)})
  private CatalogEntry alternativeCatalogEntry;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_FIRST_WEEK_OF_DELIVERY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_FIRST_WEEK_OF_DELIVERY_WRITE)})
  private Integer firstWeekOfDelivery;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_LAST_WEEK_OF_DELIVERY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_LAST_WEEK_OF_DELIVERY_WRITE)})
  private Integer latestWeekOfDelivery;

  @Column(columnDefinition = "TEXT")
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_COMMENT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_COMMENT_WRITE)})
  private String comment;

  @Enumerated(EnumType.STRING)
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_CREATION_TYPE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_CREATION_TYPE_WRITE)})
  private PreOrderCreator CreationType;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @JoinColumn(nullable = true)
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.CONTAINER_CREATED_BY_READ)})
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.CONTAINER_CREATED_BY_WRITE)})
  private User createdBy;

  @PrePersist
  private void setCreatedBy() {
    createdBy = LogInModel.getLoggedIn();
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

  public boolean isShopOrder() {
    return user.isShopUser();
  }

  // report property! don't remove
  public String getContainerInfo() {
    return getCatalogEntry().getBestelleinheit();
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
