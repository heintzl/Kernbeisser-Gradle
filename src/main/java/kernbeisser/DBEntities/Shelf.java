package kernbeisser.DBEntities;

import java.time.Instant;
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
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Shelf {
  @GeneratedValue
  @Id
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_ID_WRITE)})
  private int id;

  @Column(unique = true, nullable = false)
  @Getter(onMethod_ = {@Key(PermissionKey.SHELF_LOCATION_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SHELF_LOCATION_WRITE)})
  private int shelfNo;

  @Column(unique = true, nullable = false)
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

  @CreationTimestamp @Getter private Instant createDate;

  public static List<Shelf> getAll() {
    return DBConnection.getAll(Shelf.class);
  }

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
        .sorted(Comparator.comparingInt(Article::getKbNumber))
        .collect(Collectors.toList());
  }

  public static int createShelfNo() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery("select coalesce (max(s.shelfNo), 0) from Shelf s", Integer.class)
            .getSingleResult()
        + 1;
  }

  public Stream<ArticleStock> getAllArticleStocks(EntityManager em) {
    return getAllArticles().stream()
        .map(e -> ArticleStock.ofArticle(em, e, this))
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  public Collection<ArticleStock> getArticleStocks() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return this.getAllArticles(em).stream()
        .map(e -> ArticleStock.ofArticle(em, e, this).orElse(ArticleStock.newFromArticle(e, this)))
        .collect(Collectors.toList());
  }
}
