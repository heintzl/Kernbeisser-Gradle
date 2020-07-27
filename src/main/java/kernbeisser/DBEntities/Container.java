package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table
public class Container implements Serializable {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_ID_WRITE)})
  private int id;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_ITEM_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_ITEM_WRITE)})
  private ArticleKornkraft item;

  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_USER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_USER_WRITE)})
  private User user;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_USER_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_USER_SURCHARGE_WRITE)})
  private double userSurcharge;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_INFO_WRITE)})
  private String info;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_AMOUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_AMOUNT_WRITE)})
  private int amount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_NET_PRICE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_NET_PRICE_WRITE)})
  private double netPrice;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_PAYED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_PAYED_WRITE)})
  private boolean payed;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_DELIVERY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_DELIVERY_WRITE)})
  private Instant delivery;

  @CreationTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.CONTAINER_CREATE_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.CONTAINER_CREATE_DATE_WRITE)})
  private Instant createDate;

  public static List<Container> getAll(String condition) {
    return Tools.getAll(Container.class, condition);
  }

  public int getKBNumber() {
    List<Article> articles =
        Article.getAll("where suppliersItemNumber = " + item.getSuppliersItemNumber());
    if (articles.size() == 0) {
      return -1;
    } else {
      return articles.get(0).getKbNumber();
    }
  }

  @Override
  public String toString() {
    return "Container[" + id + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Container container = (Container) o;
    return id == container.id
        && Double.compare(container.userSurcharge, userSurcharge) == 0
        && amount == container.amount
        && Double.compare(container.netPrice, netPrice) == 0
        && payed == container.payed
        && item.equals(container.item)
        && user.equals(container.user)
        && info.equals(container.info)
        && delivery.equals(container.delivery)
        && createDate.equals(container.createDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, item, user, userSurcharge, info, amount, netPrice, payed, delivery, createDate);
  }
}
