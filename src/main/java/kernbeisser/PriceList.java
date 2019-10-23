package kernbeisser;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "PriceLists")
public class PriceList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false,insertable = false,nullable = false)
    private int pid;

    @PrimaryKeyJoinColumn
    @Column(nullable = false,unique = true)
    private String name;

    @ManyToOne
    @JoinColumn
    private PriceList superPriceList;

    @UpdateTimestamp
    private Date updateDate;

    @CreationTimestamp
    private Date createDate;

    public Date getUpdateDate() {
        return updateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PriceList getSuperPriceList() {
        return superPriceList;
    }

    public void setSuperPriceList(PriceList superPriceLists) {
        this.superPriceList = superPriceLists;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static PriceList getSingleItemPriceList(){
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select p from PriceList p where name like 'Einzelartikel'", PriceList.class).getSingleResult();
        }catch (NoResultException e){
            EntityTransaction et = em.getTransaction();
            et.begin();
            PriceList p = new PriceList();
            p.setName("Einzelartikel");
            em.persist(p);
            em.flush();
            et.commit();
            return em.createQuery("select p from PriceList p where name like 'Einzelartikel'", PriceList.class).getSingleResult();
        }
        finally {
            em.close();
        }
    }
    public static PriceList getCoveredIntakePriceList(){
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select p from PriceList p where name like 'Verdeckte Aufnahme'", PriceList.class).getSingleResult();
        }catch (NoResultException e){
            EntityTransaction et = em.getTransaction();
            et.begin();
            PriceList p = new PriceList();
            p.setName("Verdeckte Aufnahme");
            em.persist(p);
            em.flush();
            et.commit();
            return em.createQuery("select p from PriceList p where name like 'Verdeckte Aufnahme'", PriceList.class).getSingleResult();
        }
        finally {
            em.close();
        }
    }
}
