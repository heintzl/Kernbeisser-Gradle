package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "Suppliers")
public class Supplier implements Serializable {

    @Id
    @Column(updatable = false, insertable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int sid;

    @Column(unique = true)
    private String name;

    @Column
    private String phoneNumber;

    @Column
    private String fax;

    @Column
    private String address;

    @Column
    private String email;

    @Column(unique = true)
    private String shortName;

    @Column
    private int surcharge;

    @Column
    private String keeper;

    @CreationTimestamp
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

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

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(int surcharge) {
        this.surcharge = surcharge;
    }

    public String getKeeper() {
        return keeper;
    }

    public void setKeeper(String keeper) {
        this.keeper = keeper;
    }

    public static List<Supplier> getAll(String condition){
        return Tools.getAll(Supplier.class,condition);
    }
}
