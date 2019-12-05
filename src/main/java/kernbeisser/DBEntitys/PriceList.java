package kernbeisser.DBEntitys;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
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
    private Date updateDate;

    @CreationTimestamp
    private Date createDate;

    private static void savePriceList(String name){
        PriceList p = new PriceList();
        p.setName(name);
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(p);
        em.flush();
        et.commit();
        em.close();
    }
    private static PriceList getPriceList(String name) throws NoResultException{
        EntityManager em = DBConnection.getEntityManager();
        try{
            PriceList out = em.createQuery("select p from PriceList p where name like '"+name+"'",PriceList.class).getSingleResult();
            em.close();
            return out;
        }catch (NoResultException e){
            em.close();
            throw e;
        }
    }
    private static PriceList getOrCreate(String name){
        try{
            return getPriceList(name);
        }catch (NoResultException e){
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public Date getCreateDate() {
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

    public static List<PriceList> getAll(String condition){
        return Tools.getAll(PriceList.class,condition);
    }

    public Collection<PriceList> getAllPriceLists(){
        EntityManager em = DBConnection.getEntityManager();
        Collection<PriceList> out = em.createQuery("select p from PriceList p where p.superPriceList = " + getId(), PriceList.class).getResultList();
        em.close();
        return out;
    }
}
