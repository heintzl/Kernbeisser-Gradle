package kernbeisser.DBEntities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import rs.groump.Key;
import rs.groump.PermissionKey;

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
  @Getter(onMethod_ = {@Key(PermissionKey.PERMISSION_GRANT_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PERMISSION_GRANT_ID_WRITE)})
  private int id;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.PERMISSION_GRANT_ON_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PERMISSION_GRANT_ON_WRITE)})
  private Permission on;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.PERMISSION_GRANT_BY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.PERMISSION_GRANT_BY_WRITE)})
  private Permission by;

  public PermissionGrant(Permission on, Permission by) {
    this.on = on;
    this.by = by;
  }
}
