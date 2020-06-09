package kernbeisser.DBEntities;

import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;


@Table
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ArticleBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String name;

    @Column
    private String producer;

    @Column
    private double netPrice;

    @Column
    private MetricUnits metricUnits;

    @JoinColumn
    @ManyToOne
    private Supplier supplier;

    @Column
    private int suppliersItemNumber;

    @Column
    private VAT vat;

    @Column
    private int amount;

    @Column
    private Long barcode;

    @Column
    private double containerSize;

    @Column
    private double singleDeposit;

    @Column
    private double containerDeposit;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(double netPrice) {
        this.netPrice = netPrice;
    }

    public MetricUnits getMetricUnits() {
        return metricUnits;
    }

    public void setMetricUnits(MetricUnits metricUnits) {
        this.metricUnits = metricUnits;
    }

    public int getSuppliersItemNumber() {
        return suppliersItemNumber;
    }

    public void setSuppliersItemNumber(int suppliersItemNumber) {
        this.suppliersItemNumber = suppliersItemNumber;
    }

    public VAT getVat() {
        return vat;
    }

    public void setVat(VAT vat) {
        this.vat = vat;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public double getContainerSize() {
        return containerSize;
    }

    public void setContainerSize(double containerSize) {
        this.containerSize = containerSize;
    }

    public double getSingleDeposit() {
        return singleDeposit;
    }

    public void setSingleDeposit(double singleDeposit) {
        this.singleDeposit = singleDeposit;
    }

    public double getContainerDeposit() {
        return containerDeposit;
    }

    public void setContainerDeposit(double crateDeposit) {
        this.containerDeposit = crateDeposit;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
}
