package kernbeisser.DBEntities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Shelf {
  @GeneratedValue
  @Id
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_LOCATION_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_LOCATION_WRITE)})
  private String location;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_COMMENT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_COMMENT_WRITE)})
  private String comment;

  @JoinColumn
  @ManyToMany(fetch = FetchType.EAGER)
  @Getter(
      onMethod_ = {@Key({PermissionKey.SHELF_ARTICLES_READ, PermissionKey.SHELF_ARTICLES_WRITE})})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_ARTICLES_WRITE)})
  private Set<PriceList> priceLists = new HashSet<>();

  @JoinColumn
  @ManyToMany(fetch = FetchType.EAGER)
  @Getter(
      onMethod_ = {@Key({PermissionKey.SHELF_ARTICLES_READ, PermissionKey.SHELF_ARTICLES_WRITE})})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_ARTICLES_WRITE)})
  private Set<Article> articles = new HashSet<>();

  public Collection<Article> getAllArticles() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getAllArticles(em);
  }

  public Collection<Article> getAllArticles(EntityManager em) {
    return Stream.concat(
            priceLists.stream()
                .map(e -> Articles.getAllActiveArticlesFromPriceList(em, e))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(Article::getKbNumber)),
            articles.stream())
        .distinct()
        .collect(Collectors.toList());
  }

  public Stream<ArticleStock> getAllArticleStocks(EntityManager em) {
    return getAllArticles().stream()
        .map(e -> ArticleStock.ofArticle(em, e))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(ArticleStock::isNotExpired);
  }

  public double calculateTotal() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return calculateTotal(em);
  }

  public double calculateTotal(EntityManager em) {
    return getAllArticleStocks(em).mapToDouble(ArticleStock::calculateNetPrice).sum();
  }
}
