package kernbeisser.DBEntities;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

public class Inventory {
    @Id
    @GeneratedValue
    private int id;

    @CreationTimestamp
    @Column
    private Date creationTimeStamp;


    public Date getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Date creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public int getId() {
        return id;
    }
}
