package kernbeisser.DBEntities;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Shelf {
    @GeneratedValue
    @Id
    @Getter(onMethod_= {@Key(PermissionKey.SHELF_ID_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SHELF_ID_WRITE)})
    private int id;

    @Column
    @Getter(onMethod_= {@Key(PermissionKey.SHELF_LOCATION_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SHELF_LOCATION_WRITE)})
    private String location;

    @JoinColumn
    @ManyToMany
    @Getter(onMethod_= {@Key(PermissionKey.SHELF_ARTICLES_READ)})
    @Setter(onMethod_= {@Key(PermissionKey.SHELF_ARTICLES_WRITE)})
    private Set<Article> articles = new HashSet<>();

    public int getId() {
        return id;
    }

    public void addArticle(Article a){
        articles.add(a);
    }

    public void removePriceList(PriceList p){
        articles.remove(p);
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
