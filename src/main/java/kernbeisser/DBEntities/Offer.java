package kernbeisser.DBEntities;

import kernbeisser.Enums.Repeat;

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

    public int getSpecialNetPrice() {
        return specialNetPrice;
    }

    public void setSpecialNetPrice(int specialNetPrice) {
        this.specialNetPrice = specialNetPrice;
    }

    public void setRepeatMode(Repeat repeat) {
        this.repeatMode = repeat;
    }

    public Repeat getRepeatMode() {
        return repeatMode;
    }
}
