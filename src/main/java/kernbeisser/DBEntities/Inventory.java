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

public class Inventory {
    @Id
    @GeneratedValue
    @Getter(onMethod_= {@Key(PermissionKey.INVENTORY_ID_READ)})
    @Setter(value = AccessLevel.NONE, onMethod_= {@Key(PermissionKey.INVENTORY_ID_WRITE)})
    private int id;

    @CreationTimestamp
    @Column
    @Getter(onMethod_= {@Key(PermissionKey.INVENTORY_CREATION_TIME_STAMP_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.INVENTORY_CREATION_TIME_STAMP_WRITE)})
    private Instant creationTimeStamp;

}
