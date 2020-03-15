package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "catalog")
public class ArticleKornkraft implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String name;

    @Column
    private String producer;

    @Column
    //TODO save as double
    private int netPrice;

    @Column
    private MetricUnits metricUnits;

    @Column
    private int kkNumber;

    @Column
    //TODO save as double
    private boolean vatLow;

    @Column
    private int amount;

    @Column
    private String barcode;

    @Column
    private double containerSize;

    @Column
    //TODO save as double
    private int singleDeposit;

    @Column
    //TODO save as double
    private int crateDeposit;

    public static List<ArticleKornkraft> getAll(String condition) {
        return Tools.getAll(ArticleKornkraft.class, condition);
    }

    public static ArticleKornkraft getByKkNumber(int kkNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select k from ArticleKornkraft k where kkNumber = :n", ArticleKornkraft.class)
                     .setParameter("n", kkNumber)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public static ArticleKornkraft getByKbNumber(int kbNumber) {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery(
                    "select ik from ArticleKornkraft ik where ik.kkNumber = (select i.suppliersItemNumber from Article i where i.kbNumber = :n and i.supplier.shortName = 'KK')",
                    ArticleKornkraft.class)
                     .setParameter("n", kbNumber)
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetricUnits getMetricUnits() {
        return metricUnits;
    }

    public void setMetricUnits(MetricUnits metricUnits) {
        this.metricUnits = metricUnits;
    }

    public int getKkNumber() {
        return kkNumber;
    }

    public void setKkNumber(int kkNumber) {
        this.kkNumber = kkNumber;
    }

    public boolean isVatLow() {
        return vatLow;
    }

    public void setVatLow(boolean vatLow) {
        this.vatLow = vatLow;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getContainerSize() {
        return containerSize;
    }

    public void setContainerSize(double containerSize) {
        this.containerSize = containerSize;
    }

    public int getSingleDeposit() {
        return singleDeposit;
    }

    public void setSingleDeposit(int singleDeposit) {
        this.singleDeposit = singleDeposit;
    }

    public int getCrateDeposit() {
        return crateDeposit;
    }

    public void setCrateDeposit(int crateDeposit) {
        this.crateDeposit = crateDeposit;
    }

    public int getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(int netPrice) {
        this.netPrice = netPrice;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public SurchargeTable getSurcharge() {
        //TODO really expensive!
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery(
                    "select st from SurchargeTable st where st.supplier.id = :supplier and st.from <= :number and st.to >= :number",
                    SurchargeTable.class)
                     .setParameter("supplier", Supplier.getKKSupplier().getId())
                     .setParameter("number", kkNumber)
                     .setMaxResults(1)
                     .getSingleResult();
        } catch (NoResultException e) {
            return SurchargeTable.DEFAULT;
        }
    }

    public int getContainerPrice() {
        return (int) ((netPrice * ((getSurcharge().getSurcharge() / 200f) + 1)) + 0.5);
    }

    public int calculatePrice() {
        return (int) ((netPrice * ((getSurcharge().getSurcharge() / 100f) + 1)) + 0.5);
    }
}
