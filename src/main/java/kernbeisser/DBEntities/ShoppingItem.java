package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Unit;
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
    @Column
    private int rawPrice;
    @Column
    private int netPrice;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Purchase purchase;
    @Column
    private String name;
    @Column
    private int kbNumber;
    @Column
    private int itemAmount;
    @Column
    private int itemNetPrice;
    @Column
    private boolean vatLow;
    @Column
    private int itemRawPrice;
    @Column
    private Unit unit;
    @Column
    private boolean weighAble;
    @Column
    private int suppliersItemNumber;
    @Column(length = 5)
    private String shortName;

    public ShoppingItem() {
    }

    public ShoppingItem(Item item) {
        this.name = item.getName();
        this.kbNumber = item.getKbNumber();
        this.amount = item.getAmount();
        this.itemNetPrice = item.getNetPrice();
        //TODO this.rawPrice = item.getSurcharge();
        this.unit = item.getUnit();
        this.vatLow = item.isVatLow();
        this.weighAble=item.isWeighAble();
        this.itemRawPrice=item.calculatePrice();
        if(item.getSupplier()!=null)
        this.shortName=item.getSupplier().getShortName();
        this.suppliersItemNumber=item.getSuppliersItemNumber();
    }

    public ShoppingItem(Item item, int discount, int price) {
        this(item);
        this.discount = discount;
        this.netPrice = price;
    }

    public Item extractItem(){
        EntityManager em = DBConnection.getEntityManager();
        try{
            return em.createQuery("SELECT i from Item i where kbNumber = "+kbNumber,Item.class).getSingleResult();
        }catch (NoResultException e){
            return null;
        }finally {
            em.close();
        }
    }

    public static ShoppingItem getOrganic(int price) {
        ShoppingItem out;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            out = new ShoppingItem(em.createQuery("select i from Item i where name like 'Obst und Gem\u00fcse'", Item.class).getSingleResult());
        } catch (NoResultException e) {
            et.begin();
            Item organic = new Item();
            organic.setName("Obst und Gem\u00fcse");
            organic.setDeleteAllowed(false);
            organic.setKbNumber(-1);
            organic.setUnit(Unit.STACK);
            em.persist(organic);
            em.flush();
            et.commit();
            out = new ShoppingItem(em.createQuery("select  i from Item i where name like 'Obst und Gem\u00fcse'", Item.class).getSingleResult());
        }
        out.setRawPrice(price);
        out.setItemAmount(1);
        em.close();
        return out;
    }

    public static ShoppingItem getBakeryProduct(int price) {
        ShoppingItem out;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            out = new ShoppingItem(em.createQuery("select  i from Item i where name like 'Backware'", Item.class).getSingleResult());
        } catch (NoResultException e) {
            et.begin();
            Item bakeryProduct = new Item();
            bakeryProduct.setName("Backware");
            bakeryProduct.setUnit(Unit.STACK);
            bakeryProduct.setDeleteAllowed(false);
            bakeryProduct.setKbNumber(-2);
            em.persist(bakeryProduct);
            em.flush();
            et.commit();
            out = new ShoppingItem(em.createQuery("select  i from Item i where name like 'Backware'", Item.class).getSingleResult());
        }
        out.setRawPrice(price);
        out.setItemAmount(1);
        em.close();
        return out;
    }

    public static ShoppingItem getDeposit(int price) {
        ShoppingItem out;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            out = new ShoppingItem(em.createQuery("select  i from Item i where name like 'Pfand'", Item.class).getSingleResult());
        } catch (NoResultException e) {
            et.begin();
            Item deposit = new Item();
            deposit.setName("Pfand");
            deposit.setKbNumber(-3);
            deposit.setUnit(Unit.STACK);
            deposit.setDeleteAllowed(false);
            em.persist(deposit);
            em.flush();
            et.commit();
            out = new ShoppingItem(em.createQuery("select  i from Item i where name like 'Pfand'", Item.class).getSingleResult());
        }
        out.setRawPrice(price);
        out.setItemAmount(1);
        em.close();
        return out;
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

    public int getRawPrice() {
        return rawPrice;
    }

    public void setRawPrice(int rawPrice) {
        this.rawPrice = rawPrice;
    }

    public boolean isWeighAble() {
        return weighAble;
    }

    public void setWeighAble(boolean weighAble) {
        this.weighAble = weighAble;
    }

    public Unit getUnit() {
        return unit != null ? unit : Unit.NONE;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getSiid() {
        return siid;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(int amount) {
        this.itemAmount = amount;
    }


    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    @Override
    public int hashCode() {
        return kbNumber*(discount+1)*getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShoppingItem) {
            ShoppingItem item = (ShoppingItem) obj;
            return item.discount == discount && item.name.equals(name) && item.kbNumber == kbNumber;
        }else return false;
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

    public int getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(int netPrice) {
        this.netPrice = netPrice;
    }

    public int getItemRawPrice() {
        return itemRawPrice;
    }

    public void setItemRawPrice(int itemRawPrice) {
        this.itemRawPrice = itemRawPrice;
    }

    public static List<ShoppingItem> getAll(String condition){
        return Tools.getAll(ShoppingItem.class,condition);
    }
}
