package kernbeisser;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table
public class ShoppingItem {

    @Id
    private int siid;

    @ManyToOne
    @JoinColumn
    private Item item;

    @Column
    private int amount;

    @Column
    private int price;

    @JoinColumn
    @ManyToOne
    private ShoppingSession shoppingSession;

    @CreationTimestamp
    private Date createDate;

    public int getSiid() {
        return siid;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ShoppingSession getShoppingSession() {
        return shoppingSession;
    }

    public void setShoppingSession(ShoppingSession shoppingSession) {
        this.shoppingSession = shoppingSession;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
