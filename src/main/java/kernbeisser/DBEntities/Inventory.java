package kernbeisser.DBEntities;

import java.time.Instant;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Inventory {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.INVENTORY_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.INVENTORY_ID_WRITE)})
  private int id;

  @CreationTimestamp
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.INVENTORY_CREATION_TIME_STAMP_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.INVENTORY_CREATION_TIME_STAMP_WRITE)})
  private Instant creationTimeStamp;
}
