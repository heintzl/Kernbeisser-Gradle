package kernbeisser.DBEntities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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

  @Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)
  public static int get(Article article) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      return em.createQuery("select number from ArticlePrintPool where article = :a", Integer.class)
          .setParameter("a", article)
          .getSingleResult();
    } catch (NoResultException e) {
      return 0;
    }
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
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      return em.createQuery("select id from ArticlePrintPool ap where article = :a", Long.class)
          .setParameter("a", article)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public static Map<Article, Integer> getPrintPoolAsMap() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Map<Article, Integer> result = new HashMap<>();
    em.createQuery("select ap from ArticlePrintPool ap", ArticlePrintPool.class)
        .getResultStream()
        .forEach(e -> result.put(e.article, e.number));
    return result;
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
