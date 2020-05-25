package kernbeisser.DBEntities;

import kernbeisser.Enums.Repeat;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table
public class Offer implements Serializable {
    @Id
    @GeneratedValue
    private int oid;

    @Column
    private double specialNetPrice;

    @Column()
    //dangerous SQL keyword from do not rename to 'from'
    private Date fromDate;

    @Column()
    //dangerous SQL keyword from do not rename to 'to'
    private Date toDate;

    @Column
    //dangerous SQL keyword repeat do not rename to 'repeat'
    private Repeat repeatMode;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date from) {
        this.fromDate = from;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public int getOid() {
        return oid;
    }

    public double getSpecialNetPrice() {
        return specialNetPrice;
    }

    public void setSpecialNetPrice(double specialNetPrice) {
        this.specialNetPrice = specialNetPrice;
    }

    public Repeat getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(Repeat repeat) {
        this.repeatMode = repeat;
    }

    @Override
    public int hashCode() {
        return oid;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Offer)obj).oid == oid;
    }
}
