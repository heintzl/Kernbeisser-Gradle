package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.time.Instant;
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
    private Instant createDate;

    private double userSurcharge;

    public static List<Purchase> getAll(String condition) {
        return Tools.getAll(Purchase.class, condition);
    }

    public Instant getCreateDate() {
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

    public double getSum() {
        EntityManager em = DBConnection.getEntityManager();
        double sum = em.createQuery("select s from ShoppingItem s where purchase.id = :id",
                                    ShoppingItem.class)
                       .setParameter("id", sid)
                       .getResultList()
                       .stream()
                       .mapToDouble(ShoppingItem::getItemRetailPrice)
                       .sum();
        em.close();
        return sum * (1 + userSurcharge);
    }

    public double getUserSurcharge() {
        return userSurcharge;
    }

    public void setUserSurcharge(double userSurcharge) {
        this.userSurcharge = userSurcharge;
    }

}
