package kernbeisser;

import javax.persistence.*;

@Table
@Entity
public class ShoppingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int sid;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn
    private User seller;

    public int getSid() {
        return sid;
    }

    public User getCustomer() {
        return customer;
    }

    public User getSeller() {
        return seller;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public ShoppingSession(){}
    public ShoppingSession(User customer,User seller){
        setCustomer(customer);
        setSeller(seller);
    }
}
