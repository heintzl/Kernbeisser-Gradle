package kernbeisser.DBEntities;

import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
    uniqueConstraints = {
      @UniqueConstraint(
          name = "UX_Grant_on_by",
          columnNames = {"on_id", "by_id"})
    })
@EqualsAndHashCode
@NoArgsConstructor
public class PermissionGrant {
  @Id
  @GeneratedValue
  @GenericGenerator(name = "increment", strategy = "increment")
  @Getter(onMethod_ = {@Key(PermissionKey.GRANT_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.GRANT_ID_WRITE)})
  private int id;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.GRANT_ON_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.GRANT_ON_WRITE)})
  private Permission on;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.GRANT_BY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.GRANT_BY_WRITE)})
  private Permission by;

  public PermissionGrant(Permission on, Permission by) {
    this.on = on;
    this.by = by;
  }
}
