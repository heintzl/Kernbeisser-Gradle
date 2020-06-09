package kernbeisser.DBEntities;


import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Cooling;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table
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
        return out;
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


    public int getKbNumber() {
        return kbNumber;
    }

    public void setKbNumber(int kbNumber) {
        this.kbNumber = kbNumber;
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

    public double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }

    public PriceList getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }

    public ContainerDefinition getContainerDef() {
        return containerDef;
    }

    public void setContainerDef(ContainerDefinition containerDef) {
        this.containerDef = containerDef;
    }

    public int getSuppliersItemNumber() {
        return suppliersItemNumber;
    }

    public void setSuppliersItemNumber(int suppliersItemNumber) {
        this.suppliersItemNumber = suppliersItemNumber;
    }

    public boolean isWeighAble() {
        return weighAble;
    }

    public void setWeighAble(boolean weighAble) {
        this.weighAble = weighAble;
    }

    public boolean isListed() {
        return listed;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    public boolean isShowInShop() {
        return showInShop;
    }

    public void setShowInShop(boolean showInShop) {
        this.showInShop = showInShop;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isPrintAgain() {
        return printAgain;
    }

    public void setPrintAgain(boolean printAgain) {
        this.printAgain = printAgain;
    }

    public boolean isDeleteAllowed() {
        return deleteAllowed;
    }

    public void setDeleteAllowed(boolean deleteAllowed) {
        this.deleteAllowed = deleteAllowed;
    }

    public void setSpecialPriceMonth(List<Offer> specialPriceMonth) {
        this.specialPriceMonth = specialPriceMonth;
    }

    public List<Offer> getSpecialPriceMonths() {
        return specialPriceMonth;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }


    public Instant getIntake() {
        return intake;
    }

    public void setIntake(Instant intake) {
        this.intake = intake;
    }

    public Instant getLastDelivery() {
        return lastDelivery;
    }

    public void setLastDelivery(Instant lastDelivery) {
        this.lastDelivery = lastDelivery;
    }

    public Instant getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Instant deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Cooling getCooling() {
        return cooling;
    }

    public void setCooling(Cooling cooling) {
        this.cooling = cooling;
    }

    public boolean isCoveredIntake() {
        return coveredIntake;
    }

    public void setCoveredIntake(boolean coveredIntake) {
        this.coveredIntake = coveredIntake;
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    @Override
    public int hashCode() {
        return getId() + getName().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
