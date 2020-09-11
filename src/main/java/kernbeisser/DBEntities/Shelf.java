package kernbeisser.DBEntities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Shelf {
  @GeneratedValue
  @Id
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_LOCATION_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_LOCATION_WRITE)})
  private String location;

  @JoinColumn
  @ManyToMany
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_ARTICLES_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_ARTICLES_WRITE)})
  private Set<Article> articles = new HashSet<>();
}
