package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;
import java.util.List;

@Table
@Entity
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int sid;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SaleSession session;

    @CreationTimestamp
    private Date createDate;

    public static List<Purchase> getAll(String condition) {
        return Tools.getAll(Purchase.class, condition);
    }

    public Date getCreateDate() {
        return createDate;
    }

    public int getId() {
        return sid;
    }

    public SaleSession getSession() {
        return session;
    }

    public void setSession(SaleSession session) {
        this.session = session;
    }

    public Collection<ShoppingItem> getAllItems() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<ShoppingItem> out = em.createQuery("select i from ShoppingItem i where i.purchase.id = " + sid,
                                                      ShoppingItem.class).getResultList();
        em.close();
        return out;
    }

    public long getSum() {
        EntityManager em = DBConnection.getEntityManager();
        Long i = (Long) em.createQuery("select sum(i.rawPrice) from ShoppingItem i").getSingleResult();
        System.out.println(i);
        em.close();
        return i;
    }
}
