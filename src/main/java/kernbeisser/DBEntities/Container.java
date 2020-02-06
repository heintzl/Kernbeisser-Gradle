package kernbeisser.DBEntities;

import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Entity
@Table
public class Container implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn
    private ItemKK item;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column
    private String info;

    @Column
    private int amount;

    @Column
    private boolean payed;

    @Column
    private Date delivery;

    @CreationTimestamp
    private Date createDate;

    public static List<Container> getAll(String condition){
        return Tools.getAll(Container.class,condition);
    }

    public ItemKK getItem(){
        return item;
    }

    public void setItem(ItemKK item) {
        this.item = item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public Date getDelivery() {
        return delivery;
    }

    public void setDelivery(Date delivery) {
        this.delivery = delivery;
    }

    public Date getCreateDate() {
        return createDate;
    }


    public int getId() {
        return id;
    }

    public int getKBNumber(){
        List<Item> items = Item.getAll("where kbNumber = "+item.getKkNumber());
        if(items==null)return -1;
        else return items.get(0).getKbNumber();
    }

    public int getNetPrice(){
        return item.getNetPrice()*amount;
    }

    public int getPrice(){
        return item.getContainerPrice()*amount;
    }
}
