package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.VAT;
import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.NotSupportedException;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@Getter
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
    @Setter
    private Purchase purchase;

    @Column
    private String name;

    @Column
    private int kbNumber;

    @Column
    @Setter
    private int itemMultiplier = 1;

    @Column
    private double vat;

    @Column
    @Getter(AccessLevel.NONE)
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

    @Column
    @Setter
    private int shoppingCartIndex;

    @Transient
    private double singleDeposit;

    @Transient
    private double containerDeposit;

    @Transient
    private double containerSize;

    @Transient
    private int superIndex;

    public ShoppingItem(Article article, int discount, boolean hasContainerDiscount) {
        this.containerDiscount = hasContainerDiscount;
        this.name = article.getName();
        this.kbNumber = article.getKbNumber();
        this.amount = article.getAmount();
        this.itemNetPrice = article.getNetPrice();
        this.metricUnits = article.isWeighAble() ? article.getMetricUnits() : MetricUnits.PIECE;
        this.vat = article.getVat().getValue();
        this.weighAble = article.isWeighAble();
        this.surcharge = (hasContainerDiscount
                          ? article.getSurcharge() * Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue()
                          : article.getSurcharge());
        this.discount = discount;
        if (article.getSupplier() != null) {
            this.shortName = article.getSupplier().getShortName();
        }
        this.suppliersItemNumber = article.getSuppliersItemNumber();
        this.singleDeposit = article.getSingleDeposit();
        this.containerDeposit = article.getContainerDeposit();
        this.containerSize = article.getContainerSize();
        this.itemRetailPrice = calculateItemRetailPrice();
    }

    public static ShoppingItem createOrganic(double price) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            ShoppingItem out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Obst und Gem\u00fcse'", Article.class)
                      .getSingleResult(), 0, false) {
                @Override
                public void addToRetailPrice(double addedRetailPrice) {
                    ShoppingItem.addToRetailPrice(this, addedRetailPrice);
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
            deposit.setVat(VAT.LOW);
            em.persist(deposit);
            em.flush();
            et.commit();
            return createDeposit(price);
        } catch (NotSupportedException e) {
            Tools.showUnexpectedErrorWarning(e);
            return null;
        }
    }

    public static ShoppingItem createBakeryProduct(double price) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            ShoppingItem out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Backware'", Article.class)
                      .getSingleResult(), 0, false) {
                @Override
                public void addToRetailPrice(double addedRetailPrice) {
                    ShoppingItem.addToRetailPrice(this, addedRetailPrice);
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
            deposit.setVat(VAT.LOW);
            em.persist(deposit);
            em.flush();
            et.commit();
            return createDeposit(price);
        } catch (NotSupportedException e) {
            Tools.showUnexpectedErrorWarning(e);
            return null;
        }
    }

    public static ShoppingItem createDeposit(double price) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            ShoppingItem out = new ShoppingItem(
                    em.createQuery("select  i from Article i where name like 'Pfand'", Article.class).getSingleResult(),
                    0, false) {
                @Override
                public void addToRetailPrice(double addedRetailPrice) {
                    ShoppingItem.addToRetailPrice(this, addedRetailPrice);
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
            deposit.setVat(VAT.HIGH);
            em.persist(deposit);
            em.flush();
            et.commit();
            return createDeposit(price);
        } catch (NotSupportedException e) {
            Tools.showUnexpectedErrorWarning(e);
            return null;
        }
        // TODO wie wird der Pfand verbucht? Als NetPrice oder irgendwie anders?
    }

    public static List<ShoppingItem> getAll(String condition) {
        return Tools.getAll(ShoppingItem.class, condition);
    }

    private static void addToRetailPrice(ShoppingItem item, double addRetailPrice) {
        item.itemRetailPrice += Math.round(addRetailPrice * 100) / 100.;
        item.itemNetPrice = item.itemRetailPrice / (1 + item.surcharge) / (1 + item.vat);
    }

    public double getRetailPrice() {
        return itemRetailPrice * metricUnits.getBaseFactor() * itemMultiplier;
    }

    private double calculateItemRetailPrice() {
        return Math.round(100 * itemNetPrice * (1 + vat) * (1 + surcharge)) / 100. * (1 - discount / 100.);
    }

    public ShoppingItem createItemDeposit() {
        ShoppingItem deposit = createDeposit(this.singleDeposit);
        deposit.name = "    > Einzelpfand";
        deposit.superIndex = this.getShoppingCartIndex();
        deposit.itemMultiplier = this.itemMultiplier;
        return deposit;
    }

    public ShoppingItem createContainerDeposit(int number) {
        ShoppingItem deposit = createDeposit(this.containerDeposit);
        deposit.name = "    > Gebindepfand";
        deposit.superIndex = this.getShoppingCartIndex();
        deposit.itemMultiplier = number;
        return deposit;
    }

    public Article extractArticle() {
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("SELECT i from Article i where kbNumber = " + kbNumber, Article.class)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
    public ShoppingItem newInstance(){
        return new ShoppingItem(this);
    }

    public ShoppingItem(ShoppingItem other) {
        this.amount = other.amount;
        this.discount = other.discount;
        this.purchase = other.purchase;
        this.name = other.name;
        this.kbNumber = other.kbNumber;
        this.itemMultiplier = other.itemMultiplier;
        this.vat = other.vat;
        this.metricUnits = other.metricUnits;
        this.weighAble = other.weighAble;
        this.suppliersItemNumber = other.suppliersItemNumber;
        this.shortName = other.shortName;
        this.surcharge = other.surcharge;
        this.containerDiscount = other.containerDiscount;
        this.itemRetailPrice = other.itemRetailPrice;
        this.itemNetPrice = other.itemNetPrice;
        this.singleDeposit = other.singleDeposit;
        this.containerDeposit = other.containerDeposit;
        this.containerSize = other.containerSize;
        this.shoppingCartIndex = other.shoppingCartIndex;
        this.superIndex = other.superIndex;
    }

    public void addToRetailPrice(double addedRetailPrice) throws NotSupportedException {
        throw new NotSupportedException();
    }

    public MetricUnits getMetricUnits() {
        return metricUnits != null ? metricUnits : MetricUnits.NONE;
    }

    @Override
    public int hashCode() {
        return kbNumber * ((discount % 100) + 1) * amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShoppingItem) {
            ShoppingItem item = (ShoppingItem) obj;
            return item.discount == discount
                   && item.name.equals(name)
                   && item.kbNumber == kbNumber
                   && item.vat == vat
                   && item.itemRetailPrice == itemRetailPrice
                   && item.containerDiscount == containerDiscount
                   && item.superIndex == superIndex;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
