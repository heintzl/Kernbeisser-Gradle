package kernbeisser.DBEntitys;


import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Cooling;
import kernbeisser.Enums.Unit;
import kernbeisser.Enums.VAT;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table

public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, insertable = false, nullable = false)
    private int iid;

    @Column
    private String name;

    @Column(unique = true)
    private int kbNumber;

    @Column
    private int amount;

    @Column
    private int netPrice;

    @ManyToOne
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    @Column(unique = true)
    private Long barcode;

    @Column
    private int specialPriceNet;

    @Column
    private boolean vatLow;

    @Column
    private int surcharge;

    @Column
    private int singleDeposit;

    @Column
    private int crateDeposit;

    @Column
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "priceListId")
    private PriceList priceList;

    @Column
    private ContainerDefinition containerDef;

    @Column
    private double containerSize;

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

    @Column
    @ElementCollection
    private List<Boolean> specialPriceMonth = new ArrayList<>(12);

    @Column
    private int delivered;

    @Column
    @ElementCollection
    private List<Integer> invShelf = new ArrayList<>(5);

    @Column
    @ElementCollection
    private List<Integer> invStock = new ArrayList<>(5);

    @Column
    private int invPrice;

    @Column
    private Date intake;

    @Column
    private Date lastBuy;

    @Column
    private Date lastDelivery;

    @Column
    private Date deletedDate;

    @Column
    private Cooling cooling;

    @Column
    private boolean coveredIntake;

    public int calculatePrice(){
        return (int) (netPrice*((surcharge/100f)+1)*(((vatLow ? VAT.LOW.getValue() : VAT.HIGH.getValue())/100f)+1));
    }

    public int getIid() {
        return iid;
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

    public int getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(int netPrice) {
        this.netPrice = netPrice;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public int getSpecialPriceNet() {
        return specialPriceNet;
    }

    public void setSpecialPriceNet(int specialPriceNet) {
        this.specialPriceNet = specialPriceNet;
    }

    public boolean isVatLow() {
        return vatLow;
    }

    public void setVatLow(boolean vatLow) {
        this.vatLow = vatLow;
    }

    public int getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(int surcharge) {
        this.surcharge = surcharge;
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
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

    public double getContainerSize() {
        return containerSize;
    }

    public void setContainerSize(double containerSize) {
        this.containerSize = containerSize;
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

    public List<Boolean> getSpecialPriceMonth() {
        return specialPriceMonth;
    }

    public void setSpecialPriceMonth(List<Boolean> specialPriceMonth) {
        this.specialPriceMonth = specialPriceMonth;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public List<Integer> getInvShelf() {
        return invShelf;
    }

    public void setInvShelf(List<Integer> invShelf) {
        this.invShelf = invShelf;
    }

    public List<Integer> getInvStock() {
        return invStock;
    }

    public void setInvStock(List<Integer> invStock) {
        this.invStock = invStock;
    }

    public int getInvPrice() {
        return invPrice;
    }

    public void setInvPrice(int invPrice) {
        this.invPrice = invPrice;
    }

    public Date getIntake() {
        return intake;
    }

    public void setIntake(Date intake) {
        this.intake = intake;
    }

    public Date getLastBuy() {
        return lastBuy;
    }

    public void setLastBuy(Date lastBuy) {
        this.lastBuy = lastBuy;
    }

    public Date getLastDelivery() {
        return lastDelivery;
    }

    public void setLastDelivery(Date lastDelivery) {
        this.lastDelivery = lastDelivery;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
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
        return iid + name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
