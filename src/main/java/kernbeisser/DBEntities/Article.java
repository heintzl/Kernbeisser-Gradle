package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Cooling;
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
  The surcharge describes the percentage which becomes added when the article gets bought,
  based on the kind of article sugar products have a higher surcharge than cornflakes, because
  sugar products aren't that healthy as cornflakes
   */
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SURCHARGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SURCHARGE_WRITE)})
  private double surcharge;

  /*
  describes the kind of the article and group them
   */
  @ManyToOne
  @JoinColumn(name = "priceListId")
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRICE_LIST_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRICE_LIST_WRITE)})
  private PriceList priceList;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEF_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEF_WRITE)})
  private ContainerDefinition containerDef;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_WEIGH_ABLE_WRITE)})
  private boolean weighable;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_LISTED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_LISTED_WRITE)})
  private boolean listed;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOW_IN_SHOP_WRITE)})
  private boolean showInShop;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_WRITE)})
  private boolean deleted;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRINT_AGAIN_WRITE)})
  private boolean printAgain;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETE_ALLOWED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETE_ALLOWED_WRITE)})
  private boolean deleteAllowed;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_LOSS_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_LOSS_WRITE)})
  private int loss;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_WRITE)})
  private String info;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SOLD_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SOLD_WRITE)})
  private int sold;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELIVERED_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELIVERED_WRITE)})
  private int delivered;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_INTAKE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_INTAKE_WRITE)})
  private Instant intake;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_LAST_DELIVERY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_LAST_DELIVERY_WRITE)})
  private Instant lastDelivery;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_DELETED_DATE_WRITE)})
  private Instant deletedDate;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_COOLING_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_COOLING_WRITE)})
  private Cooling cooling;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_COVERED_INTAKE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_COVERED_INTAKE_WRITE)})
  private boolean coveredIntake;

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
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select i from Article i where barcode = :n", Article.class)
          .setParameter("n", barcode)
          .getSingleResult();
    } catch (NoResultException f) {
      return null;
    } finally {
      em.close();
    }
  }

  @Override
  public String toString() {
    return Tools.decide(this::getName, "ArtikelBase[" + super.toString() + "]");
  }
}
