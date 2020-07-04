package kernbeisser.DBEntities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Shelf {
    @GeneratedValue
    @Id
    private int id;

    @Column
    private String location;

    @JoinColumn
    @ManyToMany
    private Set<Article> articles = new HashSet<>();

    public int getId() {
        return id;
    }

    public void addArticle(Article a){
        articles.add(a);
    }

    public void removePriceList(PriceList p){
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
