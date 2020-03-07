package kernbeisser.DBEntities;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table
public class Offer {
    @Id
    @GeneratedValue
    private int oid;

    @Column
    private int specialNetPrice;

    @Column(name = "\"from\"")
    private Date from;

    @Column(name = "\"to\"")
    private Date to;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public int getOid() {
        return oid;
    }

    public int getSpecialNetPrice() {
        return specialNetPrice;
    }

    public void setSpecialNetPrice(int specialNetPrice) {
        this.specialNetPrice = specialNetPrice;
    }
}
