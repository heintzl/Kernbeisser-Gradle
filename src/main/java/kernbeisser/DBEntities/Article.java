package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import lombok.*;

/*
 extends from the main article structure ArticleBase which extends Article and ArticleKornkraft
 the Article class contains additional statistic fields which aren't required for the all Articles
 and only used for Articles which are constantly in use of Kernbeisser
*/
@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
public class Article extends ArticleBase {
  /*
  the Kernbeisser number is a unique index for use in the shop.
  It is a way to identify Articles and is sorted in priceLists
  and categories.
  */
  @Column(unique = true)
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_KB_NUMBER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_KB_NUMBER_WRITE)})
  private int kbNumber;

  /*
  describes the kind of the article and group them
   */
  @ManyToOne
  @JoinColumn
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRICE_LIST_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRICE_LIST_WRITE)})
  private PriceList priceList;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_WRITE)})
  private boolean weighable;

  // boolean isInCatalog()

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_WRITE)})
  private boolean showInShop;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_ACTIVE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_ACTIVE_WRITE)})
  private boolean active;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_DATE_WRITE)})
  private Instant activeStateChange;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_VERIFIED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_VERIFIED_WRITE)})
  private boolean verified;

  public static List<Article> getAll(String condition) {
    return Tools.getAll(Article.class, condition);
  }

  private static TypedQuery<Article> createQuery(EntityManager em, String search) {
    return em.createQuery(
            "select i from Article i where kbNumber = :n"
                + " or suppliersItemNumber = :n"
                + " or i.supplier.shortName like :s"
                + " or i.supplier.name like :s"
                + " or UPPER(i.name) like :ds"
                + " or mod(barcode,:bl) = :n"
                + " or UPPER( i.priceList.name) like :u"
                + " order by i.name asc",
            Article.class)
        .setParameter("n", Tools.tryParseInteger(search))
        .setParameter(
            "bl",
            Tools.tryParseInteger(search) > 0
                ? Math.pow(10, Math.ceil(Math.log10(Tools.tryParseInteger(search))))
                : 1)
        .setParameter("s", search + "%")
        .setParameter("ds", (search.length() > 3 ? "%" + search + "%" : search + "%").toUpperCase())
        .setParameter("u", search.toUpperCase() + "%");
  }

  public static Collection<Article> defaultSearch(String search, int maxResults) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<Article> out = createQuery(em, search).setMaxResults(maxResults).getResultList();
    em.close();
    return Proxy.getSecureInstances(out);
  }

  public static Collection<Article> getDefaultAll(
      String search, Predicate<Article> articlePredicate, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<Article> out =
        createQuery(em, search)
            .getResultStream()
            .filter(articlePredicate)
            .limit(max)
            .collect(Collectors.toCollection(ArrayList::new));
    em.close();
    return Proxy.getSecureInstances(out);
  }

  public static Article getByKbNumber(int kbNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select i from Article i where kbNumber = :n", Article.class)
          .setParameter("n", kbNumber)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    } finally {
      em.close();
    }
  }

  public static Article getBySuppliersItemNumber(int suppliersNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select i from Article i where suppliersItemNumber = :n", Article.class)
          .setParameter("n", suppliersNumber)
          .getSingleResult();
    } catch (NoResultException e) {
      return null;
    } finally {
      em.close();
    }
  }

  public static Article getByBarcode(long barcode) {
    return getGenericByBarcode(barcode, Article.class);
  }

  @Override
  public String toString() {
    return Tools.decide(this::getName, "ArtikelBase[" + super.toString() + "]");
  }

  public static Article fromArticleBase(
      ArticleBase ab, boolean weighable, PriceList priceList, int kbNumber) {
    Article article = new Article();
    Tools.copyInto(ArticleBase.class, ab, article);
    article.setId(0);
    article.setWeighable(weighable);
    article.setPriceList(priceList);
    article.setKbNumber(kbNumber);
    article.setActive(true);
    article.setVerified(false);
    article.setActiveStateChange(Instant.now());
    return article;
  }

  public static Article nextArticleTo(int suppliersItemNumber, Supplier supplier) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return nextArticleTo(em, suppliersItemNumber, supplier);
  }

  public static Article nextArticleTo(
      EntityManager em, int suppliersItemNumber, Supplier supplier) {
    return em.createQuery(
            "select a from Article a where supplier = :s order by abs(a.suppliersItemNumber - :sn) asc",
            Article.class)
        .setParameter("s", supplier)
        .setParameter("sn", suppliersItemNumber)
        .setMaxResults(1)
        .getResultStream()
        .findAny()
        .orElse(null);
  }
}
