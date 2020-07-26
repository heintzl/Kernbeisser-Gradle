package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Suppliers")
public class Supplier implements Serializable {

    @Id
    @Column(updatable = false, insertable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_SID_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_SID_WRITE)})
    private int sid;

    @Column(unique = true)
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_NAME_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_NAME_WRITE)})
    private String name;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_PHONE_NUMBER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_PHONE_NUMBER_WRITE)})
    private String phoneNumber;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_FAX_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_FAX_WRITE)})
    private String fax;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_ADDRESS_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_ADDRESS_WRITE)})
    private String address;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_EMAIL_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_EMAIL_WRITE)})
    private String email;

    @Column(unique = true)
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_SHORT_NAME_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_SHORT_NAME_WRITE)})
    private String shortName;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_SURCHARGE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_SURCHARGE_WRITE)})
    private int surcharge;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_KEEPER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_KEEPER_WRITE)})
    private String keeper;

    @CreationTimestamp
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_CREATE_DATE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_CREATE_DATE_WRITE)})
    private Instant createDate;

    @UpdateTimestamp
    @Getter(onMethod_= {@Key(PermissionKey.SUPPLIER_UPDATE_DATE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SUPPLIER_UPDATE_DATE_WRITE)})
    private Instant updateDate;

    public static Supplier getKKSupplier() {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select s from Supplier s where s.name like 'KK'", Supplier.class).getSingleResult();
        } catch (NoResultException e) {
            EntityTransaction et = em.getTransaction();
            Supplier s = new Supplier();
            s.setName("KK");
            et.begin();
            em.persist(s);
            em.flush();
            et.commit();
            return em.createQuery("select s from Supplier s where s.name like 'KK'", Supplier.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public static List<Supplier> getAll(String condition) {
        return Tools.getAll(Supplier.class, condition);
    }

    @Override
    public String toString() {
        return Tools.decide(this::getName,"Lieferant["+sid+"]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Supplier supplier = (Supplier) o;
        return sid == supplier.sid &&
               surcharge == supplier.surcharge &&
               Objects.equals(name, supplier.name) &&
               Objects.equals(phoneNumber, supplier.phoneNumber) &&
               Objects.equals(fax, supplier.fax) &&
               Objects.equals(address, supplier.address) &&
               Objects.equals(email, supplier.email) &&
               Objects.equals(shortName, supplier.shortName) &&
               Objects.equals(keeper, supplier.keeper) &&
               Objects.equals(createDate, supplier.createDate) &&
               Objects.equals(updateDate, supplier.updateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid, name, phoneNumber, fax, address, email, shortName, surcharge, keeper, createDate,
                            updateDate);
    }
}
