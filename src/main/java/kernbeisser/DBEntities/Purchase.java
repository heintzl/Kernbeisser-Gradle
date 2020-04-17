package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Table
@Entity
public class Purchase implements ValueChange{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int sid;

    @ManyToOne
    @JoinColumn(nullable = false)
    private SaleSession session;

    @CreationTimestamp
    private Date createDate;

    private double userSurcharge;

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
        return sum * (1+userSurcharge);
    }

    public double getUserSurcharge() {
        return userSurcharge;
    }

    public void setUserSurcharge(double userSurcharge) {
        this.userSurcharge = userSurcharge;
    }

    @Override
    public User getFrom() {
        return session.getCustomer();
    }

    @Override
    public User getTo() {
        return null;
    }

    @Override
    public double getValue() {
        return getSum();
    }

    @Override
    public LocalDate getDate() {
        return createDate.toLocalDate();
    }

    @Override
    public String getInfo() {
        return "";
    }
}
