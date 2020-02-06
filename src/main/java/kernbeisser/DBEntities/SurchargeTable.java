package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Table
@Entity
public class SurchargeTable implements Serializable, Cloneable {

    public static final SurchargeTable DEFAULT;

    static {
        SurchargeTable standard = new SurchargeTable();
        standard.from = -1;
        standard.to = -1;
        standard.name = "DEFAULT";
        standard.supplier = null;
        standard.surcharge = 7;
        DEFAULT = standard;
    }

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

    public static Collection<SurchargeTable> defaultSearch(String s, int max){
        EntityManager em = DBConnection.getEntityManager();
        Collection<SurchargeTable> out = em.createQuery("select s from SurchargeTable s where s.name like :search or s.supplier.name like :search or s.supplier.shortName like :search",SurchargeTable.class)
                .setParameter("search",s+"%")
                .setMaxResults(max)
                .getResultList();
        em.close();
        return out;
    }

}
