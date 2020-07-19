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
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "PriceLists")
public class PriceList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, insertable = false, nullable = false)
    @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_PID_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_PID_WRITE)})
    private int pid;

    @PrimaryKeyJoinColumn
    @Column(nullable = false, unique = true)
    @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_NAME_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_NAME_WRITE)})
    private String name;

    @ManyToOne
    @JoinColumn
    @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_SUPER_PRICE_LIST_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_SUPER_PRICE_LIST_WRITE)})
    private PriceList superPriceList;

    @UpdateTimestamp
    @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_UPDATE_DATE_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_UPDATE_DATE_WRITE)})
    private Instant updateDate;

    @CreationTimestamp
    @Getter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_PID_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.PRICE_LIST_PID_WRITE)})
    private Instant createDate;

    public static void savePriceList(String name) {
        savePriceList(name, null);
    }

    public static void savePriceList(String priceListName, PriceList superPriceList) {
        PriceList p = new PriceList();
        p.setName(priceListName);
        p.setSuperPriceList(superPriceList);
        Tools.runInSession(em -> em.persist(p));
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


    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public Collection<PriceList> getAllPriceLists() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<PriceList> out = em.createQuery(
                "select p from PriceList p where p.superPriceList = " + getPid() + " order by p.name asc",
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

    @Override
    public String toString() {
        return Tools.decide(this::getName, "Preisliste[" + pid + "]");
    }

}
