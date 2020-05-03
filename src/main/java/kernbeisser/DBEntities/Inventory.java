package kernbeisser.DBEntities;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;
import java.time.Instant;

public class Inventory {
    @Id
    @GeneratedValue
    private int id;

    @CreationTimestamp
    @Column
    private Instant creationTimeStamp;


    public Instant getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Instant creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public int getId() {
        return id;
    }
}
