package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

/*
 extends from the main article structure ArticleBase which extends Article and ArticleKornkraft
 the Article class contains additional statistic fields which aren't required for the all Articles
 and only used for Articles which are constantly in use of Kernbeisser
*/
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"supplier_id", "suppliersItemNumber"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
public class Article {

  @Id
  @GeneratedValue(generator = "increment")
  @GenericGenerator(name = "increment", strategy = "increment")
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_ID_WRITE)})
  private int id;

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
  // maybe rename to splittable, because it describes if a article should not always become sold as
  // one entire piece
  private boolean weighable;

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

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_NAME_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_NAME_WRITE)})
  private String name;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRODUCER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_PRODUCER_WRITE)})
  private String producer;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_NET_PRICE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_NET_PRICE_WRITE)})
  private double netPrice;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_METRIC_UNITS_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_METRIC_UNITS_WRITE)})
  private MetricUnits metricUnits;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIER_WRITE)})
  private Supplier supplier;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_WRITE)})
  private int suppliersItemNumber;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_VAT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_VAT_WRITE)})
  private VAT vat;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_AMOUNT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_AMOUNT_WRITE)})
  private int amount;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_BARCODE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_BARCODE_WRITE)})
  private Long barcode;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_SIZE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_SIZE_WRITE)})
  private double containerSize;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SINGLE_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SINGLE_DEPOSIT_WRITE)})
  private double singleDeposit;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEPOSIT_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_CONTAINER_DEPOSIT_WRITE)})
  private double containerDeposit;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_INFO_WRITE)})
  private String info;

  @Column @UpdateTimestamp @Getter private Instant updateDate;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.SURCHARGE_TABLE_SUPPLIER_WRITE)})
  private SurchargeGroup surchargeGroup;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOP_RANGE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.ARTICLE_SHOP_RANGE_WRITE)})
  private ShopRange shopRange;

  @Getter @Setter private Double obsoleteSurcharge;

  public static List<Article> getAll(String condition) {
    return Tools.getAll(Article.class, condition);
  }

  private static TypedQuery<Article> createQuery(EntityManager em, String search) {
    return em.createQuery(
            "select i from Article i where kbNumber = :n"
                + " or suppliersItemNumber = :n"
                + " or UPPER(i.name) like :ds"
                + " or barcode = :l"
                + " or MOD(barcode,:bl) = :n"
                + " or UPPER( i.priceList.name) like :u"
                + " order by i.name asc",
            Article.class)
        .setParameter("n", Tools.tryParseInt(search))
        .setParameter(
            "bl",
            Tools.tryParseInt(search) > 0
                ? Math.pow(10, Math.ceil(Math.log10(Tools.tryParseInt(search))))
                : 1)
        .setParameter("l", Tools.tryParseLong(search))
        .setParameter("ds", (search.length() > 3 ? "%" + search + "%" : search + "%").toUpperCase())
        .setParameter("u", search.toUpperCase() + "%");
  }

  // implement later
  public static javax.persistence.criteria.Predicate defaultSearchAlgorithm(
      EntityManager em, String search, PriceList p, boolean inShopRange) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Article> criteriaQuery = cb.createQuery(Article.class);
    Root<Article> root = criteriaQuery.from(Article.class);
    return cb.and(
        Tools.asAvailable(
            true,
            javax.persistence.criteria.Predicate.class,
            () ->
                cb.or(
                    Tools.asAvailable(
                        true,
                        javax.persistence.criteria.Predicate.class,
                        () -> cb.equal(root.get("kbNumber"), Integer.parseInt(search)),
                        () -> cb.equal(root.get("suppliersItemNumber"), Integer.parseInt(search)),
                        () -> cb.like(root.get("supplier.shortName"), search + "%"),
                        () -> cb.like(root.get("supplier.name"), search + "%"),
                        () ->
                            cb.equal(
                                cb.upper(root.get("name")),
                                (search.length() > 3 ? "%" + search + "%" : search + "%")
                                    .toUpperCase()),
                        () -> cb.equal(cb.upper(root.get("priceList.name")), search.toUpperCase()),
                        () ->
                            cb.equal(
                                cb.mod(
                                    root.get("barcode"),
                                    (int)
                                        (Math.pow(
                                            10, Math.ceil(Math.log10(Integer.parseInt(search)))))),
                                search.toUpperCase()))),
            () -> {
              if (p == null) return null;
              return cb.equal(root.get("priceList"), p);
            },
            () -> {
              if (!inShopRange) return null;
              return cb.isTrue(root.get("shopRange"));
            }));
  }

  public static Collection<Article> defaultSearch(String search, int maxResults) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return createQuery(em, search).setMaxResults(maxResults).getResultList();
  }

  public static Collection<Article> getDefaultAll(
      String search, Predicate<Article> articlePredicate, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return createQuery(em, search)
        .getResultStream()
        .filter(articlePredicate)
        .limit(max)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public boolean isShopRange() {
    return shopRange == ShopRange.IN_RANGE || shopRange == ShopRange.PERMANENT_RANGE;
  }

  public static Optional<Article> getByKbNumber(int kbNumber, boolean filterShopRange) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select i from Article i where kbNumber = :n", Article.class)
        .setParameter("n", kbNumber)
        .getResultStream()
        .filter(a -> !(filterShopRange && !a.isShopRange()))
        .findAny();
  }

  public static Optional<Article> getBySuppliersItemNumber(Supplier supplier, int suppliersNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return getBySuppliersItemNumber(supplier, suppliersNumber, em);
  }

  public static Optional<Article> getBySuppliersItemNumber(
      Supplier supplier, int suppliersNumber, EntityManager em) {
    EntityTransaction et = em.getTransaction();
    et.begin();
    try {
      return Optional.of(
          em.createQuery(
                  "select i from Article i where suppliersItemNumber = :n and supplier  = :s",
                  Article.class)
              .setParameter("s", supplier)
              .setParameter("n", suppliersNumber)
              .getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  public static Article getByBarcode(long barcode) throws NoResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select a from Article a where barcode = :b", Article.class)
        .setParameter("b", barcode)
        .getSingleResult();
  }

  public Instant getLastDelivery() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select i from ShoppingItem i where purchase.id is null and suppliersItemNumber = :k order by i.createDate desc",
            ShoppingItem.class)
        .setParameter("k", this.getSuppliersItemNumber())
        .getResultStream()
        .findFirst()
        .orElseGet(ShoppingItem::new)
        .getCreateDate();
  }

  public double getPriceFor(MetricUnits m, double amount) {
    return getNetPrice() * amount * m.getBaseFactor();
  }

  @Override
  public String toString() {
    return Tools.optional(this::getName).orElse("ArtikelBase[" + super.toString() + "]");
  }

  public static Article nextArticleTo(int suppliersItemNumber, Supplier supplier) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
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

  public Collection<Offer> getAllOffers() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select o from Offer o where article.id = :id", Offer.class)
        .setParameter("id", this.id)
        .getResultList();
  }

  public double getOfferNetPrice() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select o from Offer o where o.article.id = :id and :d between fromDate and toDate",
            Offer.class)
        .setParameter("id", id)
        .setParameter("d", Instant.now())
        .getResultStream()
        .findAny()
        .map(Offer::getSpecialNetPrice)
        .orElse(-999.0);
  }
}
