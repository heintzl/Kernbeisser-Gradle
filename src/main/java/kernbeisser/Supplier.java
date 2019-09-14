package kernbeisser;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Suppliers")
public class Supplier implements Serializable {

    @Id
    @Column(updatable = false,insertable = false,nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int sid;

    @Column(unique = true)
    private String name;

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Supplier getKKSupplier(){
        EntityManager em = DBConnection.getEntityManager();
        try{
            return em.createQuery("select s from Supplier s where s.name like 'KK'",Supplier.class).getSingleResult();
        }catch (NoResultException e){
            EntityTransaction et = em.getTransaction();
            Supplier s = new Supplier();
            s.setName("KK");
            et.begin();
            em.persist(s);
            em.flush();
            et.commit();
            return em.createQuery("select s from Supplier s where s.name like 'KK'",Supplier.class).getSingleResult();
        }
        finally {
            em.close();
        }
    }
}
