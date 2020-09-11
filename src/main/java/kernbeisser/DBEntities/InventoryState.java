package kernbeisser.DBEntities;

import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class InventoryState {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.INVENTORY_STATE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.INVENTORY_STATE_ID_WRITE)})
  private int id;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.INVENTORY_STATE_ARTICLE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.INVENTORY_STATE_ARTICLE_WRITE)})
  private Article article;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.INVENTORY_STATE_COUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.INVENTORY_STATE_COUNT_WRITE)})
  private int count;
}
