package kernbeisser.DBEntities;
// TODO HEI Review bakery/organic VAT
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import javax.transaction.NotSupportedException;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
public class ShoppingItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long siid;

    @Column
    private int amount;

    @Column
    private int discount;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Purchase purchase;

    @Column
    private String name;

    @Column
    private int kbNumber;

    @Column
    private int itemMultiplier = 1;

    @Column
    private double vat;

    @Column
    private MetricUnits metricUnits;

    @Column
    private boolean weighAble;

    @Column
    private int suppliersItemNumber;

    @Column(length = 5)
    private String shortName;

    @Column
    private double surcharge;

    @Column
    private boolean containerDiscount;

    @Column
    private double itemRetailPrice;

    @Column
    private double itemNetPrice;

    public ShoppingItem() {
    }

    public boolean getContainerDiscount() {
        return containerDiscount;
    }

    public ShoppingItem(Article article, int discount, boolean hasContainerDiscount) {
        this.containerDiscount = hasContainerDiscount;
        this.name = article.getName();
        this.kbNumber = article.getKbNumber();
        this.amount = article.getAmount();
        this.itemNetPrice = article.getNetPrice();
        this.metricUnits = article.isWeighAble() ? article.getMetricUnits() : MetricUnits.PIECE;
        this.vat = article.getVAT().getValue();
        this.weighAble = article.isWeighAble();
        this.surcharge = (hasContainerDiscount ? article.getSurcharge() * Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue() : article.getSurcharge());
        this.discount = discount;
        if (article.getSupplier() != null) {
            this.shortName = article.getSupplier().getShortName();
        }
        this.suppliersItemNumber = article.getSuppliersItemNumber();
        this.itemRetailPrice = calculateItemRetailPrice();
    }

    public double getRetailPrice() {
        return itemRetailPrice * metricUnits.getBaseFactor() * itemMultiplier;
    }

    public double getItemRetailPrice() {
        return itemRetailPrice;
    }

    private double calculateItemRetailPrice(){
        return Math.round(100 * itemNetPrice * (1+vat) * (1+surcharge))/100. * (1 - discount/100.);
    }


    public static ShoppingItem createOrganic(double price) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            ShoppingItem out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Obst und Gem\u00fcse'", Article.class).getSingleResult(),0,false){
                @Override
                public void addToRetailPrice(double addedRetailPrice) {
                    ShoppingItem.addToRetailPrice(this,addedRetailPrice);
                }
            };
            out.addToRetailPrice(price);
            return out;
        } catch (NoResultException e) {
            et.begin();
            Article deposit = new Article();
            deposit.setName("Obst und Gem\u00fcse");
            deposit.setKbNumber(-1);
            deposit.setMetricUnits(MetricUnits.PIECE);
            deposit.setDeleteAllowed(false);
            deposit.setVAT(VAT.LOW);
            em.persist(deposit);
            em.flush();
            et.commit();
            return createDeposit(price);
        } catch (NotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ShoppingItem createBakeryProduct(double price) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            ShoppingItem out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Backware'", Article.class).getSingleResult(),0,false){
                @Override
                public void addToRetailPrice(double addedRetailPrice) {
                    ShoppingItem.addToRetailPrice(this,addedRetailPrice);
                }
            };
            out.addToRetailPrice(price);
            return out;
        } catch (NoResultException e) {
            et.begin();
            Article deposit = new Article();
            deposit.setName("Backware");
            deposit.setKbNumber(-2);
            deposit.setMetricUnits(MetricUnits.PIECE);
            deposit.setDeleteAllowed(false);
            deposit.setVAT(VAT.LOW);
            em.persist(deposit);
            em.flush();
            et.commit();
            return createDeposit(price);
        } catch (NotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ShoppingItem createDeposit(double price) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            ShoppingItem out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Pfand'", Article.class).getSingleResult(),0,false){
                @Override
                public void addToRetailPrice(double addedRetailPrice) {
                    ShoppingItem.addToRetailPrice(this,addedRetailPrice);
                }
            };
            out.addToRetailPrice(price);
            return out;
        } catch (NoResultException e) {
            et.begin();
            Article deposit = new Article();
            deposit.setName("Pfand");
            deposit.setKbNumber(-3);
            deposit.setMetricUnits(MetricUnits.PIECE);
            deposit.setDeleteAllowed(false);
            deposit.setVAT(VAT.HIGH);
            em.persist(deposit);
            em.flush();
            et.commit();
            return createDeposit(price);
        } catch (NotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        // TODO wie wird der Pfand verbucht? Als NetPrice oder irgendwie anders?
    }

    public static List<ShoppingItem> getAll(String condition) {
        return Tools.getAll(ShoppingItem.class, condition);
    }

    public Article extractArticle() {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("SELECT i from Article i where kbNumber = " + kbNumber, Article.class).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void addToRetailPrice(double addedRetailPrice) throws NotSupportedException{
        throw new NotSupportedException();
    }

    private static void addToRetailPrice(ShoppingItem item,double addRetailPrice){
        item.itemRetailPrice += Math.round(addRetailPrice*100)/100.;
        item.itemNetPrice = item.itemRetailPrice / (1+item.surcharge) / (1+item.vat);
    }

    public String getName() {
        return name;
    }

    public int getKbNumber() {
        return kbNumber;
    }

    public int getAmount() {
        return amount;
    }

    public double getItemNetPrice() {
        return itemNetPrice;
    }

    public double getVat() {
        return vat;
    }

    public boolean isWeighable() {
        return weighAble;
    }

    public MetricUnits getMetricUnits() {
        return metricUnits != null ? metricUnits : MetricUnits.NONE;
    }

    public long getSiid() {
        return siid;
    }

    public int getItemMultiplier() {
        return itemMultiplier;
    }

    public void setItemMultiplier(int amount) {
        this.itemMultiplier = amount;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    @Override
    public int hashCode() {
        return kbNumber * ((discount % 100) + 1) * amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShoppingItem) {
            ShoppingItem item = (ShoppingItem) obj;
            return item.discount == discount && item.name.equals(name) && item.kbNumber == kbNumber;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public int getDiscount() {
        return discount;
    }

    public int getSuppliersItemNumber() {
        return suppliersItemNumber;
    }

    public String getShortName() {
        return shortName;
    }

    public double getSurcharge() {
        return surcharge;
    }
}
