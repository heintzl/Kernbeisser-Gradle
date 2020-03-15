package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
public class ShoppingItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int siid;
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
    //TODO save as double
    private int itemNetPrice;
    @Column
    //TODO save as double
    private boolean vatLow;
    @Column
    private MetricUnits metricUnits;
    @Column
    private boolean weighAble;
    @Column
    private int suppliersItemNumber;
    @Column(length = 5)
    private String shortName;

    @Column
    private int surcharge;

    public ShoppingItem() {
    }

    public ShoppingItem(Article article) {
        this.name = article.getName();
        this.kbNumber = article.getKbNumber();
        this.amount = article.getAmount();
        this.itemNetPrice = article.getNetPrice();
        //TODO this.rawPrice = item.getSurcharge();
        this.metricUnits = article.getMetricUnits();
        this.vatLow = article.isVatLow();
        this.weighAble = article.isWeighAble();
        this.surcharge = article.getSurcharge();
        if (article.getSupplier() != null) {
            this.shortName = article.getSupplier().getShortName();
        }
        this.suppliersItemNumber = article.getSuppliersItemNumber();
    }

    public ShoppingItem(Article article, int discount, int price) {
        this(article);
        this.discount = discount;
    }

    public static ShoppingItem createOrganic(int price) {
        ShoppingItem out;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            out = new ShoppingItem(
                    em.createQuery("select i from Article i where name like 'Obst und Gem\u00fcse'", Article.class)
                      .getSingleResult());
        } catch (NoResultException e) {
            et.begin();
            Article organic = new Article();
            organic.setName("Obst und Gem\u00fcse");
            organic.setDeleteAllowed(false);
            organic.setKbNumber(-1);
            organic.setMetricUnits(MetricUnits.STACK);
            em.persist(organic);
            em.flush();
            et.commit();
            out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Obst und Gem\u00fcse'", Article.class)
                      .getSingleResult());
        }
        out.setItemMultiplier(1);
        out.setItemNetPrice(price);
        em.close();
        return out;
    }

    public static ShoppingItem createBakeryProduct(int price) {
        ShoppingItem out;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Backware'", Article.class).getSingleResult());
        } catch (NoResultException e) {
            et.begin();
            Article bakeryProduct = new Article();
            bakeryProduct.setName("Backware");
            bakeryProduct.setMetricUnits(MetricUnits.STACK);
            bakeryProduct.setDeleteAllowed(false);
            bakeryProduct.setKbNumber(-2);
            em.persist(bakeryProduct);
            em.flush();
            et.commit();
            out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Backware'", Article.class).getSingleResult());
        }
        out.setItemMultiplier(1);
        out.setItemNetPrice(price);
        em.close();
        return out;
    }

    public static ShoppingItem createDeposit(int price) {
        ShoppingItem out;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Pfand'", Article.class).getSingleResult());
        } catch (NoResultException e) {
            et.begin();
            Article deposit = new Article();
            deposit.setName("Pfand");
            deposit.setKbNumber(-3);
            deposit.setMetricUnits(MetricUnits.STACK);
            deposit.setDeleteAllowed(false);
            em.persist(deposit);
            em.flush();
            et.commit();
            out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Pfand'", Article.class).getSingleResult());
        }
        out.setItemMultiplier(1);
        out.setItemNetPrice(price);
        em.close();
        return out;
    }

    public static List<ShoppingItem> getAll(String condition) {
        return Tools.getAll(ShoppingItem.class, condition);
    }

    public Article extractItem() {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("SELECT i from Article i where kbNumber = " + kbNumber, Article.class).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKbNumber() {
        return kbNumber;
    }

    public void setKbNumber(int kbNumber) {
        this.kbNumber = kbNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getItemNetPrice() {
        return itemNetPrice;
    }

    public void setItemNetPrice(int netPrice) {
        this.itemNetPrice = netPrice;
    }

    public boolean isVatLow() {
        return vatLow;
    }

    public void setVatLow(boolean vatLow) {
        this.vatLow = vatLow;
    }

    public boolean isWeighable() {
        return weighAble;
    }

    public void setWeighAble(boolean weighAble) {
        this.weighAble = weighAble;
    }

    public MetricUnits getMetricUnits() {
        return metricUnits != null ? metricUnits : MetricUnits.NONE;
    }

    public void setMetricUnits(MetricUnits metricUnits) {
        this.metricUnits = metricUnits;
    }

    public int getSiid() {
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

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getSuppliersItemNumber() {
        return suppliersItemNumber;
    }

    public void setSuppliersItemNumber(int suppliersItemNumber) {
        this.suppliersItemNumber = suppliersItemNumber;
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
}
