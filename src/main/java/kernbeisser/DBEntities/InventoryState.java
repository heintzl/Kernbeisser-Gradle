package kernbeisser.DBEntities;

import javax.persistence.*;

@Table
@Entity
public class InventoryState {
    @Id
    @GeneratedValue
    private int id;

    @Column
    @ManyToOne
    private Article article;

    @Column
    private int count;


    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }
}
