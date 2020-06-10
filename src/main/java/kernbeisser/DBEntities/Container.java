package kernbeisser.DBEntities;

import kernbeisser.Useful.Tools;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Table
@Data
public class Container implements Serializable {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
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

    public int getKBNumber() {
        List<Article> articles = Article.getAll("where suppliersItemNumber = " + item.getSuppliersItemNumber());
        if (articles == null || articles.size() == 0) {
            return -1;
        } else {
            return articles.get(0).getKbNumber();
        }
    }

}
