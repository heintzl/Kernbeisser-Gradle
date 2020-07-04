package kernbeisser.DBEntities;

import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import lombok.*;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;


@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private MetricUnits metricUnits;

    @JoinColumn
    @ManyToOne
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIER_WRITE)})
    private Supplier supplier;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_SUPPLIERS_ITEM_NUMBER_WRITE)})
    private int suppliersItemNumber;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_VAT_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.ARTICLE_BASE_VAT_WRITE)})
    private VAT vat;

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
}
