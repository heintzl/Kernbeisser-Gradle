package kernbeisser;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "catalog")
public class ItemKK implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String name;

    @Column
    private String producer;

    @Column
    private int netPrice;

    @Column
    private Unit unit;

    @Column
    private int kkNumber;

    @Column
    private boolean vatLow;

    @Column
    private int amount;

    @Column
    private String barcode;

    @Column
    private double containerSize;

    @Column
    private int singleDeposit;

    @Column
    private int crateDeposit;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
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

}
