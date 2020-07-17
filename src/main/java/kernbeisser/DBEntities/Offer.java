package kernbeisser.DBEntities;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Repeat;
import kernbeisser.Security.Key;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table
public class Offer implements Serializable {
    @Id
    @GeneratedValue
    @Getter(onMethod_= {@Key(PermissionKey.OFFER_OID_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.OFFER_OID_WRITE)})
    private int oid;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.OFFER_SPECIAL_NET_PRICE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.OFFER_SPECIAL_NET_PRICE_WRITE)})
    private double specialNetPrice;

    @Column()
    //dangerous SQL keyword 'from' do not rename to 'from'
    @Getter(onMethod_= {@Key(PermissionKey.OFFER_FROM_DATE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.OFFER_FROM_DATE_WRITE)})
    private Date fromDate;

    @Column()
    //dangerous SQL keyword 'to' do not rename to 'to'
    @Getter(onMethod_= {@Key(PermissionKey.OFFER_TO_DATE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.OFFER_TO_DATE_WRITE)})
    private Date toDate;

    @Column
    //dangerous SQL keyword 'repeat' do not rename to 'repeat'
    @Getter(onMethod_= {@Key(PermissionKey.OFFER_REPEAT_MODE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.OFFER_REPEAT_MODE_WRITE)})
    private Repeat repeatMode;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Offer offer = (Offer) o;
        return oid == offer.oid &&
               Double.compare(offer.specialNetPrice, specialNetPrice) == 0 &&
               fromDate.equals(offer.fromDate) &&
               toDate.equals(offer.toDate) &&
               repeatMode == offer.repeatMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oid, specialNetPrice, fromDate, toDate, repeatMode);
    }
}
