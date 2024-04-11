package kernbeisser.DBEntities;

import static kernbeisser.DBConnection.ExpressionFactory.max;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.Shelf_;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.ActuallyCloneable;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Shelf implements ActuallyCloneable {
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

  @ManyToMany(fetch = FetchType.EAGER)
  @Getter()
  @Setter()
  private Set<PriceList> priceLists = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @Getter()
  @Setter()
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
                .map(e -> ArticleRepository.getAllActiveArticlesFromPriceList(em, e))
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(Article::getKbNumber)),
            articles.stream())
        .distinct()
        .sorted(Comparator.comparingInt(Article::getKbNumber))
        .collect(Collectors.toList());
  }

  public static int createShelfNo() {
    return QueryBuilder.select(Shelf.class, max(Shelf_.shelfNo))
            .getSingleResultOptional()
            .map(tuple -> tuple.get(0, Integer.class))
            .orElse(0)
        + 1;
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

  @Override
  public Shelf clone() throws CloneNotSupportedException {
    try {
      return (Shelf) super.clone();
    } catch (CloneNotSupportedException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }
}
