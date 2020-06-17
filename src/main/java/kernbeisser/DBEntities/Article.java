package kernbeisser.DBEntities;


import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Cooling;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article extends ArticleBase{
    @Column(unique = true)
    private int kbNumber;

    @Column
    private double surcharge;

    @ManyToOne
    @JoinColumn(name = "priceListId")
    private PriceList priceList;

    @Column
    private ContainerDefinition containerDef;


    @Column
    private int suppliersItemNumber;

    @Column
    private boolean weighAble;

    @Column
    private boolean listed;

    @Column
    private boolean showInShop;

    @Column
    private boolean deleted;

    @Column
    private boolean printAgain;

    @Column
    private boolean deleteAllowed;

    @Column
    private int loss;

    @Column
    private String info;

    @Column
    private int sold;

    @JoinColumn
    @OneToMany(fetch = FetchType.EAGER)
    private List<Offer> specialPriceMonth = new ArrayList<>();

    @Column
    private int delivered;

    @Column
    private Instant intake;

    @Column
    private Instant lastDelivery;

    @Column
    private Instant deletedDate;

    @Column
    private Cooling cooling;

    @Column
    private boolean coveredIntake;

    public static List<Article> getAll(String condition) {
        return Tools.getAll(Article.class, condition);
    }

    public static Collection<Article> defaultSearch(String search, int maxResults) {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Article> out = em.createQuery(
                "select i from Article i where kbNumber = :n or suppliersItemNumber = :n or i.supplier.shortName like :s or i.supplier.name like :s or UPPER(i.name) like :ds or mod(barcode, 10000) = :n or UPPER( i.priceList.name) like :u order by i.name asc",
                Article.class
        )
                                    .setParameter("n", Tools.tryParseInteger(search))
                                    .setParameter("s", search + "%")
                                    .setParameter("ds",(search.length()>3 ? "%"+search+"%" : search+"%").toUpperCase())
                                    .setParameter("u",search.toUpperCase()+"%")
                                    .setMaxResults(maxResults)
                                    .getResultList();
        em.close();
        return Proxy.getSecureInstances(out);
    }

    public static Article getByKbNumber(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select i from Article i where kbNumber = :n", Article.class)
                     .setParameter("n", kbNumber)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public static Article getBySuppliersItemNumber(int suppliersNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select i from Article i where suppliersItemNumber = :n", Article.class)
                     .setParameter("n", suppliersNumber)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


    public SurchargeTable getSurchargeTable() {
        //TODO really expensive!
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery(
                    "select st from SurchargeTable st where st.supplier.id = :supplier and st.from <= :number and st.to >= :number",
                    SurchargeTable.class)
                     .setParameter("supplier", getSupplier() != null ? getSupplier().getId() : -1)
                     .setParameter("number", getSuppliersItemNumber())
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return SurchargeTable.DEFAULT;
        }
    }


    @Override
    public int hashCode() {
        return getId() + getName().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    private Article(Article other) {
        this.kbNumber = other.kbNumber;
        this.surcharge = other.surcharge;
        this.priceList = other.priceList;
        this.containerDef = other.containerDef;
        this.suppliersItemNumber = other.suppliersItemNumber;
        this.weighAble = other.weighAble;
        this.listed = other.listed;
        this.showInShop = other.showInShop;
        this.deleted = other.deleted;
        this.printAgain = other.printAgain;
        this.deleteAllowed = other.deleteAllowed;
        this.loss = other.loss;
        this.info = other.info;
        this.sold = other.sold;
        this.specialPriceMonth = other.specialPriceMonth;
        this.delivered = other.delivered;
        this.intake = other.intake;
        this.lastDelivery = other.lastDelivery;
        this.deletedDate = other.deletedDate;
        this.cooling = other.cooling;
        this.coveredIntake = other.coveredIntake;
        super.setData(other);
    }

    public Article newInstance(){
        return new Article(this);
    }
}
