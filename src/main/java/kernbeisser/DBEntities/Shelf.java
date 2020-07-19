package kernbeisser.DBEntities;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Shelf shelf = (Shelf) o;
        return id == shelf.id &&
               location.equals(shelf.location) &&
               articles.equals(shelf.articles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, articles);
    }
}
