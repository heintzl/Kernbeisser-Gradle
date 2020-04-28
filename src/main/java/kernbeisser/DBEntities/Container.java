package kernbeisser.DBEntities;

import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Entity
@Table
public class Container implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn
    private ArticleKornkraft item;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column
    private double userSurcharge;

    @Column
    private String info;

    @Column
    private int amount;

    @Column
    private double netPrice;

    @Column
    private boolean payed;

    @Column
    private Instant delivery;

    @CreationTimestamp
    private Instant createDate;

    public static List<Container> getAll(String condition) {
        return Tools.getAll(Container.class, condition);
    }

    public ArticleKornkraft getItem() {
        return item;
    }

    public void setItem(ArticleKornkraft item) {
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

    public Instant getDelivery() {
        return delivery;
    }

    public void setDelivery(Instant delivery) {
        this.delivery = delivery;
    }

    public Instant getCreateDate() {
        return createDate;
    }


    public int getId() {
        return id;
    }

    public int getKBNumber() {
        List<Article> articles = Article.getAll("where suppliersItemNumber = " + item.getKkNumber());
        if (articles == null || articles.size() == 0) {
            return -1;
        } else {
            return articles.get(0).getKbNumber();
        }
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(int overriddenPrice) {
        this.netPrice = overriddenPrice;
    }


    public double getUserSurcharge() {
        return userSurcharge;
    }

    public void setUserSurcharge(double userSurcharge) {
        this.userSurcharge = userSurcharge;
    }

}
