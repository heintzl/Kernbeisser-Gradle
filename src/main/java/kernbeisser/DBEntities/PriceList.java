package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "PriceLists")
public class PriceList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, insertable = false, nullable = false)
    private int pid;

    @PrimaryKeyJoinColumn
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn
    private PriceList superPriceList;

    @UpdateTimestamp
    private Instant updateDate;

    @CreationTimestamp
    private Instant createDate;

    public static void savePriceList(String name) {
        savePriceList(name, null);
    }

    public static void savePriceList(String priceListName, PriceList superPriceList) {
        PriceList p = new PriceList();
        p.setName(priceListName);
        p.setSuperPriceList(superPriceList);
        Tools.runInSession(em ->  em.persist(p));
    }

    public static void deletePriceList(PriceList toDelete) {
        Tools.runInSession(em -> em.remove(em.contains(toDelete) ? toDelete : em.merge(toDelete)));
    }

    private static PriceList getPriceList(String name) throws NoResultException {
        EntityManager em = DBConnection.getEntityManager();
        try {
            PriceList out = em.createQuery("select p from PriceList p where name like '" + name + "'", PriceList.class)
                              .getSingleResult();
            em.close();
            return out;
        } catch (NoResultException e) {
            em.close();
            throw e;
        }
    }

    private static PriceList getOrCreate(String name) {
        try {
            return getPriceList(name);
        } catch (NoResultException e) {
            savePriceList(name);
            return getPriceList(name);
        }
    }

    public static PriceList getSingleItemPriceList() {
        return getOrCreate("Einzelartikel");
    }

    public static PriceList getCoveredIntakePriceList() {
        return getOrCreate("Verdeckte Aufnahme");
    }

    public static List<PriceList> getAll(String condition) {
        return Tools.getAll(PriceList.class, condition);
    }

    public static Collection<PriceList> getAllHeadPriceLists() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<PriceList> out = em.createQuery("select p from PriceList p where p.superPriceList = null",
                                                   PriceList.class).getResultList();
        em.close();
        return out;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public Instant getCreateDate() {
        return createDate;
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

    @Override
    public String toString() {
        return name;
    }

    public Collection<PriceList> getAllPriceLists() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<PriceList> out = em.createQuery("select p from PriceList p where p.superPriceList = " + getId()+" order by p.name asc",
                                                   PriceList.class).getResultList();
        em.close();
        return out;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PriceList priceList = (PriceList) o;
        return pid == priceList.pid;
    }
}
