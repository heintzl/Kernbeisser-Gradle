package kernbeisser.DBEntities;

import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.VAT;
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
    @Setter(AccessLevel.NONE)
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
