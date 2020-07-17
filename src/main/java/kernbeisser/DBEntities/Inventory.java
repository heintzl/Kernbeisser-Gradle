package kernbeisser.DBEntities;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;
import java.time.Instant;
import java.util.Objects;

public class Inventory {
    @Id
    @GeneratedValue
    @Getter(onMethod_= {@Key(PermissionKey.INVENTORY_ID_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.INVENTORY_ID_WRITE)})
    private int id;

    @CreationTimestamp
    @Column
    @Getter(onMethod_= {@Key(PermissionKey.INVENTORY_CREATION_TIME_STAMP_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.INVENTORY_CREATION_TIME_STAMP_WRITE)})
    private Instant creationTimeStamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Inventory inventory = (Inventory) o;
        return id == inventory.id &&
               creationTimeStamp.equals(inventory.creationTimeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationTimeStamp);
    }
}
