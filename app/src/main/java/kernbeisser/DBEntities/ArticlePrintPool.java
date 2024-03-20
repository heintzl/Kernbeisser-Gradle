package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Types.ArticlePrintPoolField;
import kernbeisser.Security.StaticPermissionChecks;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table
@Data
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
public class ArticlePrintPool {

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_WRITE)})
  private long id;

  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_WRITE)})
  @ManyToOne
  @JoinColumn(unique = true)
  private Article article;

  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_WRITE)})
  @Column
  private int number;

  public ArticlePrintPool(Article article, int number) {
    this.article = article;
    this.number = number;
  }

  public static int get(Article article) {
    StaticPermissionChecks.getStaticInstance().checkShouldReadArticlePrintPoolAgain();
    return QueryBuilder.selectAll(ArticlePrintPool.class)
        .where(ArticlePrintPoolField.article.eq(article))
        .getSingleResultOptional()
        .map(ArticlePrintPool::getNumber)
        .orElse(0);
  }

  private static void deleteAll(EntityManager em) {
    em.createQuery("delete from ArticlePrintPool").executeUpdate();
  }

  public static void clear() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    deleteAll(em);
    et.commit();
  }

  private static Long getArticlePrintPoolId(Article article) {
    return QueryBuilder.selectAll(ArticlePrintPool.class)
        .where(ArticlePrintPoolField.article.eq(article))
        .getSingleResultOptional()
        .map(ArticlePrintPool::getId)
        .orElse(null);
  }

  public static Map<Article, Integer> getPrintPoolAsMap() {
    return QueryBuilder.selectAll(ArticlePrintPool.class).getResultList().stream()
        .collect(Collectors.toMap(e -> e.article, e -> e.number));
  }

  public static void setPrintPoolFromMap(Map<Article, Integer> newPrintPool) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<Article> printPoolArticles = getPrintPoolAsMap().keySet();
    for (Article a : printPoolArticles) {
      if (!newPrintPool.containsKey(a) || newPrintPool.get(a) < 1) {
        em.remove(em.find(ArticlePrintPool.class, getArticlePrintPoolId(a)));
      }
    }

    for (Article a : newPrintPool.keySet()) {
      Long id = getArticlePrintPoolId(a);
      if (newPrintPool.get(a) > 0) {
        if (id == null) {
          em.persist(new ArticlePrintPool(a, newPrintPool.get(a)));
        } else {
          ArticlePrintPool articlePrintPool = em.find(ArticlePrintPool.class, id);
          articlePrintPool.setNumber(newPrintPool.get(a));
          em.merge(articlePrintPool);
        }
      }
    }
  }
}
