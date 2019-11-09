package kernbeisser.DBEntitys;

import kernbeisser.DBEntitys.User;

import javax.persistence.*;

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

    public User getCustomer() {
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
