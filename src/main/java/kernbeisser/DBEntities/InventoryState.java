package kernbeisser.DBEntities;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Table
@Entity
public class InventoryState {
    @Id
    @GeneratedValue
    @Getter(onMethod_= {@Key(PermissionKey.INVENTORY_STATE_ID_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.INVENTORY_STATE_ID_WRITE)})
    private int id;

    @JoinColumn
    @ManyToOne
    @Getter(onMethod_= {@Key(PermissionKey.INVENTORY_STATE_ARTICLE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.INVENTORY_STATE_ARTICLE_WRITE)})
    private Article article;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.INVENTORY_STATE_COUNT_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.INVENTORY_STATE_COUNT_WRITE)})
    private int count;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InventoryState that = (InventoryState) o;
        return id == that.id &&
               count == that.count &&
               article.equals(that.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, article, count);
    }
}
