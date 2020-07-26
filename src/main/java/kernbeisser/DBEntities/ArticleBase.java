package kernbeisser.DBEntities;

import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.Objects;


@Table
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ArticleBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_ID_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_ID_WRITE)})
    private int id;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_NAME_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_NAME_WRITE)})
    private String name;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_PRODUCER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_PRODUCER_WRITE)})
    private String producer;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_NET_PRICE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_NET_PRICE_WRITE)})
    private double netPrice;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_METRIC_UNITS_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_METRIC_UNITS_WRITE)})
    private MetricUnits metricUnits = MetricUnits.PIECE;

    @JoinColumn
    @ManyToOne
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIER_WRITE)})
    private Supplier supplier;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_WRITE)})
    private int suppliersItemNumber;

    @Column(nullable = false)
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_VAT_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_VAT_WRITE)})
    private VAT vat = VAT.LOW;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_AMOUNT_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_AMOUNT_WRITE)})
    private int amount;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_BARCODE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_BARCODE_WRITE)})
    private Long barcode;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_SIZE_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_SIZE_WRITE)})
    private double containerSize;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SINGLE_DEPOSIT_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SINGLE_DEPOSIT_WRITE)})
    private double singleDeposit;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_DEPOSIT_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_CONTAINER_DEPOSIT_WRITE)})
    private double containerDeposit;


    public void setData(ArticleBase other){
        this.name = other.name;
        this.producer = other.producer;
        this.netPrice = other.netPrice;
        this.metricUnits = other.metricUnits;
        this.supplier = other.supplier;
        this.suppliersItemNumber = other.suppliersItemNumber;
        this.vat = other.vat;
        this.amount = other.amount;
        this.barcode = other.barcode;
        this.containerSize = other.containerSize;
        this.singleDeposit = other.singleDeposit;
        this.containerDeposit = other.containerDeposit;
    }
    @Override
    public String toString(){
        return Tools.decide(this::getName,"ArtikelBase["+id+"]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArticleBase that = (ArticleBase) o;
        return id == that.id &&
               Double.compare(that.netPrice, netPrice) == 0 &&
               suppliersItemNumber == that.suppliersItemNumber &&
               amount == that.amount &&
               Double.compare(that.containerSize, containerSize) == 0 &&
               Double.compare(that.singleDeposit, singleDeposit) == 0 &&
               Double.compare(that.containerDeposit, containerDeposit) == 0 &&
               Objects.equals(name, that.name) &&
               Objects.equals(producer, that.producer) &&
               metricUnits == that.metricUnits &&
               Objects.equals(supplier, that.supplier) &&
               vat == that.vat &&
               Objects.equals(barcode, that.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, producer, netPrice, metricUnits, supplier, suppliersItemNumber, vat, amount,
                            barcode,
                            containerSize, singleDeposit, containerDeposit);
    }
}
