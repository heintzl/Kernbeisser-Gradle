package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.EntityWrapper.ObjectState;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;

public class Articles {

  public static Article getEmptyArticle() {
    Article empty = new Article();
    Access.runWithAccessManager(
        AccessManager.NO_ACCESS_CHECKING,
        () -> {
          empty.setName("Kein Artikel gefunden");
          empty.setSurchargeGroup(new SurchargeGroup());
        });
    return empty;
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

  public static Optional<ObjectState<Article>> getByKbNumber(
      int kbNumber, boolean filterShopRange) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Optional.ofNullable(
        em.createQuery("select i from Article i where kbNumber = :n", Article.class)
            .setParameter("n", kbNumber)
            .getResultStream()
            .filter(a -> !(filterShopRange && !a.getShopRange().isVisible()))
            .findAny()
            .map(ObjectState::currentState)
            .orElseGet(() -> searchInArticleHistoryForKbNumber(em, kbNumber).orElse(null)));
  }

  public static Optional<ObjectState<Article>> searchInArticleHistoryForKbNumber(
      EntityManager em, int kbNumber) {
    return ((Optional<Object[]>)
            AuditReaderFactory.get(em).createQuery()
                .forRevisionsOfEntity(Article.class, false, true)
                .add(AuditEntity.property("kbNumber").eq(kbNumber))
                .addOrder(AuditEntity.property("updateDate").desc()).getResultList().stream()
                .findFirst())
        .map(e -> ObjectState.wrap(((Article) e[0]), ((DefaultRevisionEntity) e[1]).getId()))
        .map(
            e -> {
              e.getValue().getSurchargeGroup().getSurcharge();
              return e;
            });
  }

  public static Optional<Article> getBySuppliersItemNumber(Supplier supplier, int suppliersNumber) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getBySuppliersItemNumber(supplier, suppliersNumber, em);
  }

  public static Optional<Article> getBySuppliersItemNumber(
      Supplier supplier, int suppliersNumber, EntityManager em) {
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

  public static Optional<Article> getByBarcode(long barcode) throws NoResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<Article> articles =
        em.createQuery("select a from Article a where barcode = :b", Article.class)
            .setParameter("b", barcode)
            .getResultList();
    return Optional.ofNullable(
        articles.stream()
            .filter(Articles::articleIsActiveOffer)
            .findAny()
            .orElse(articles.stream().findFirst().orElse(null)));
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

  public static int nextFreeKBNumber(EntityManager em) {
    return em.createQuery(
            "select a.kbNumber+1 from Article a where not exists (select b from Article b where b.kbNumber = a.kbNumber+1)",
            Integer.class)
        .setMaxResults(1)
        .getSingleResult();
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

  public static boolean articleIsActiveOffer(Article article) {
    return article.isOffer() && findOfferOn(article).isPresent();
  }

  public static Optional<Offer> findOfferOn(Article article) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select o from Offer o where o.offerArticle = :o", Offer.class)
        .setParameter("o", article).getResultList().stream()
        .filter(e -> e.getFromDate().isBefore(Instant.now()))
        .filter(e -> e.getToDate().isAfter(Instant.now()))
        .findAny();
  }

  public static Instant getLastDelivery(Article article) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select i from ShoppingItem i where purchase.id is null and suppliersItemNumber = :k order by i.createDate desc",
            ShoppingItem.class)
        .setParameter("k", article.getSuppliersItemNumber())
        .getResultStream()
        .findFirst()
        .orElseGet(ShoppingItem::new)
        .getCreateDate();
  }

  public static Article createOfferArticle(
      Article base, double specialNetPrice, Instant from, Instant to) {
    if (base.isOffer()) throw new IllegalArgumentException("article is already a offer");
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Optional<Article> offerArticle =
        Tools.optional(
            em.createQuery(
                "select a from Article a where a.suppliersItemNumber = :s and offer = true",
                Article.class));
    Article newOfferArticle =
        offerArticle.orElseGet(() -> withValidKBNumber(Tools.clone(base), em));
    newOfferArticle.setNetPrice(specialNetPrice);
    newOfferArticle.setOffer(true);
    Offer offer = new Offer();
    offer.setOfferArticle(newOfferArticle);
    offer.setParentArticle(base);
    offer.setFromDate(from);
    offer.setToDate(to);
    em.persist(newOfferArticle);
    em.persist(offer);
    return newOfferArticle;
  }

  public static Article withValidKBNumber(Article article, EntityManager em) {
    article.setKbNumber(Articles.nextFreeKBNumber(em));
    return article;
  }

  public static void addToPrintPool(Collection<Article> print) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Article article : print) {
      Article persistence = em.find(Article.class, article.getId());
      persistence.setPrintPool(true);
      em.persist(persistence);
    }
  }

  public static Collection<Article> getPrintPool() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select a from Article a where a.printPool = true", Article.class)
        .getResultList();
  }

  public static long getArticlePrintPoolSize() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Long result =
        em.createQuery(
                "select sum (case when printPool = true then 1 else 0 end) from Article a",
                Long.class)
            .getSingleResult();
    return result == null ? 0 : result;
  }

  public static void replacePrintPool(Collection<Article> newPrintPool) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.createQuery("update Article set printPool = false").executeUpdate();
    for (Article article : newPrintPool) {
      Article persistence = em.find(Article.class, article.getId());
      persistence.setPrintPool(true);
      em.persist(persistence);
    }
  }
}
