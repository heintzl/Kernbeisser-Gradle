package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;

import javax.persistence.*;

@Entity
@Table
public class VATConstant {
    private static final double STANDARD_VAT_LOW = 0.07;
    private static final double STANDARD_VAT_HIGH = 0.19;
    private static VATConstant low;
    private static VATConstant high;

    private VATConstant(int id,double vat){
        this.id = id;
        this.value = vat;
    }

    @Id
    private int id;

    @Column
    private double value;

    @Deprecated
    //Only Hibernate access don't initialize object
    public VATConstant() {}


    public static VATConstant getHigh() {
        if(high==null)high = loadHighVAT();
        return high;
    }

    public static VATConstant getLow(){
        if(low==null)low = loadLowVAT();
        return low;
    }

    private static VATConstant loadHighVAT(){
        EntityManager em = DBConnection.getEntityManager();
        try {
            VATConstant high = em.createQuery("select v from VATConstant v where id = 1", VATConstant.class)
                                 .getSingleResult();
            em.close();
            return high;
        } catch (NoResultException e) {
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.persist(new VATConstant(1, STANDARD_VAT_HIGH));
            em.flush();
            et.commit();
            em.close();
            return loadHighVAT();
        }

    }


    private static VATConstant loadLowVAT(){
        EntityManager em = DBConnection.getEntityManager();
        try {
            VATConstant high = em.createQuery("select v from VATConstant v where id = 0", VATConstant.class)
                                .getSingleResult();
            em.close();
            return high;
        } catch (NoResultException e) {
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.persist(new VATConstant(0, STANDARD_VAT_LOW));
            em.flush();
            et.commit();
            em.close();
            return loadLowVAT();
        }

    }

    public double getValue() {
        return value;
    }

    public int getId() {
        return id;
    }
}
