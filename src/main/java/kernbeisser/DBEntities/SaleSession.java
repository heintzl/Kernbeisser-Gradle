package kernbeisser.DBEntities;

import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class SaleSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int sSid;

    @ManyToOne
    @JoinColumn
    private User customer;

    @ManyToOne
    @JoinColumn
    private User seller;

    public static List<SaleSession> getAll(String condition) {
        return Tools.getAll(SaleSession.class, condition);
    }

    public User  getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public int getId() {
        return sSid;
    }
}
