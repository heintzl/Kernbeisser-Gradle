package kernbeisser.DBEntitys;

import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table
@Entity
public class SurchargeTable implements Serializable, Cloneable {


    public SurchargeTable(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int stid;

    @Column
    private int surcharge;

    @Column(name = "\"from\"")
    private int from;

    @Column(name = "\"to\"")
    private int to;

    @Column
    private String name;

    @JoinColumn
    @ManyToOne
    private Supplier supplier;

    public int getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(int surcharge) {
        this.surcharge = surcharge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStid() {
        return stid;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public static List<SurchargeTable> getAll(String condition){
        return Tools.getAll(SurchargeTable.class,condition);
    }

    public SurchargeTable(SurchargeTable surchargeTable){
        this.setName(surchargeTable.getName());
        this.setSurcharge(surchargeTable.getSurcharge());
        this.setName(surchargeTable.getName());
        this.setTo(surchargeTable.getTo());
        this.setFrom(surchargeTable.getFrom());
        this.setSupplier(surchargeTable.getSupplier());
    }

    @Override
    public SurchargeTable clone(){
        try {
            return (SurchargeTable) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
