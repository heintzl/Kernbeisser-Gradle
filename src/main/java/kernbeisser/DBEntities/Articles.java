package kernbeisser.DBEntities;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.EntityWrapper.ObjectState;
import kernbeisser.Enums.*;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;

public class Articles {

  private Articles() {}

  public static Article getEmptyArticle() {
    Article empty = new Article();
    Access.runWithAccessManager(
        AccessManager.NO_ACCESS_CHECKING,
        () -> {
          empty.setName("Kein Artikel gefunden");
          empty.setSurchargeGroup(new SurchargeGroup());
          empty.setAmount(0);
          empty.setNetPrice(0);
          empty.setMetricUnits(MetricUnits.NONE);
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

  public static boolean isKkArticle(Article article) {
    Supplier supplier = article.getSupplier();
    return (supplier != null && supplier.equals(Supplier.getKKSupplier()));
  }

  public static Optional<ObjectState<Article>> getByKbNumber(
      EntityManager em, int kbNumber, boolean filterShopRange) {
    return Optional.ofNullable(
        em.createQuery("select i from Article i where kbNumber = :n", Article.class)
            .setParameter("n", kbNumber)
            .getResultStream()
            .filter(a -> !(filterShopRange && !a.getShopRange().isVisible()))
            .findAny()
            .map(ObjectState::currentState)
            .orElseGet(() -> searchInArticleHistoryForKbNumber(em, kbNumber).orElse(null)));
  }

  public static Optional<ObjectState<Article>> getByKbNumber(
      int kbNumber, boolean filterShopRange) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getByKbNumber(em, kbNumber, filterShopRange);
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

  public static Optional<Article> getByKkItemNumber(int suppliersNumber) {
    return getBySuppliersItemNumber(Supplier.getKKSupplier(), suppliersNumber);
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

  public static Article getOrCreateRawPriceArticle(RawPrice rawPrice) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    List<Integer> rawPriceIdentifiers =
        Arrays.stream(ArticleConstants.class.getEnumConstants())
            .filter(e -> e != ArticleConstants.CUSTOM_PRODUCT)
            .map(ArticleConstants::getUniqueIdentifier)
            .collect(Collectors.toList());
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      return em.createQuery(
              "select  i from Article i where name = :n and kbNumber IN (:c)", Article.class)
          .setParameter("n", rawPrice.getName())
          .setParameter("c", rawPriceIdentifiers)
          .getSingleResult();
    } catch (NoResultException e) {
      ArticleConstants identifierEnum = ArticleConstants.CUSTOM_PRODUCT;
      AtomicReference<Supplier> supplier = new AtomicReference<>();
      VAT vat = VAT.LOW;
      switch (rawPrice) {
        case SOLIDARITY:
          supplier.set(Supplier.getSolidaritySupplier());
          identifierEnum = ArticleConstants.SOLIDARITY;
          break;
        case BAKERY:
          supplier.set(Supplier.getBakerySupplier());
          identifierEnum = ArticleConstants.BAKERY;
          break;
        case DEPOSIT:
          supplier.set(Supplier.getDepositSupplier());
          identifierEnum = ArticleConstants.DEPOSIT;
          vat = VAT.HIGH;
          break;
        case PRODUCE:
          supplier.set(Supplier.getProduceSupplier());
          identifierEnum = ArticleConstants.PRODUCE;
          break;
        case ITEM_DEPOSIT:
        case CONTAINER_DEPOSIT:
          return getOrCreateRawPriceArticle(RawPrice.DEPOSIT);
      }
      EntityTransaction et = em.getTransaction();
      et.begin();
      Article article = new Article();
      article.setName(rawPrice.getName());
      article.setKbNumber(identifierEnum.getUniqueIdentifier());
      article.setMetricUnits(MetricUnits.NONE);
      article.setVat(vat);
      article.setSupplier(supplier.get());
      article.setSuppliersItemNumber(identifierEnum.getUniqueIdentifier());
      Access.runWithAccessManager(
          AccessManager.NO_ACCESS_CHECKING,
          () -> article.setSurchargeGroup(supplier.get().getOrPersistDefaultSurchargeGroup(em)));
      article.setShopRange(ShopRange.NOT_IN_RANGE);
      em.persist(article);
      em.flush();
      et.commit();
      return getOrCreateRawPriceArticle(rawPrice);
    } catch (Exception o) {
      Tools.showUnexpectedErrorWarning(o);
      return null;
    }
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

  public static Map<Integer, Instant> getLastDeliveries() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Map<Integer, Instant> result = new HashMap<>();
    em.createQuery(
            "select kbNumber, max(createDate) from ShoppingItem i where purchase_id is null group by i.kbNumber order by kbNumber",
            Tuple.class)
        .getResultStream()
        .forEach(t -> result.put(t.get(0, Integer.class), t.get(1, Instant.class)));
    return result;
  }

  public static double getContainerSurchargeReduction() {
    return Setting.CONTAINER_SURCHARGE_REDUCTION.getDoubleValue();
  }

  public static double calculateRetailPrice(
      double netPrice, VAT vat, double surcharge, double discount, boolean preordered)
      throws NullPointerException {
    return netPrice * (1 + vat.getValue()) * (1 + surcharge) * (1 - discount / 100.);
  }

  public static double calculateUnroundedArticleNetPrice(Article article, boolean preordered) {
    return article.getNetPrice()
        * (preordered && article.isWeighable()
            ? getSafeAmount(article) * article.getMetricUnits().getBaseFactor()
            : 1.0);
  }

  public static double calculateArticleNetPrice(Article article, boolean preordered) {
    return Tools.roundCurrency(calculateUnroundedArticleNetPrice(article, preordered));
  }

  public static double calculateArticleRetailPrice(
      Article article, double discount, boolean preordered) {
    return Tools.roundCurrency(
        calculateRetailPrice(
            calculateUnroundedArticleNetPrice(article, preordered),
            article.getVat(),
            article.getSurchargeGroup().getSurcharge()
                * (preordered ? getContainerSurchargeReduction() : 1.0),
            discount,
            preordered));
  }

  public static String getShortBarcode(Article article) {
    Long barcode = article.getBarcode();
    if (barcode == null) {
      return "";
    }
    String barcodeString = Long.toString(barcode);
    return barcodeString.substring(Math.max(barcodeString.length() - 4, 0));
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

  public static Map<Long, Integer> calculateAmounts() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<Object[]> out =
        em.createQuery("select s.articleId, s.itemMultiplier from ShoppingItem s").getResultList();
    HashMap<Long, Integer> amountMapper = new HashMap<>();
    for (Object[] objects : out) {
      long articleId = (long) objects[0];
      int amount = objects[1] == null ? 0 : (int) objects[1];
      Integer before = amountMapper.get(articleId);
      amountMapper.put(articleId, before == null ? amount : before + amount);
    }
    return amountMapper;
  }

  public static long calculateCurrentAmount(Article article) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Long result =
        em.createQuery(
                "select sum(s.itemMultiplier) from ShoppingItem s where s.articleId = :aid",
                Long.class)
            .setParameter("aid", (long) article.getId())
            .getSingleResult();
    return result == null ? 0 : -result;
  }

  public static Collection<Article> getPrintPool() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select a from Article a where a in (select ap.article from ArticlePrintPool ap)",
            Article.class)
        .getResultList();
  }

  public static long getArticlePrintPoolSize() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Long result =
        em.createQuery("select sum (number) from ArticlePrintPool ap", Long.class)
            .getSingleResult();
    return result == null ? 0 : result;
  }

  public static Collection<Article> getAllActiveArticlesFromPriceList(
      EntityManager em, PriceList priceList) {
    if (!Setting.INVENTORY_LOOK_FOR_ARTICLE_USAGE.getBooleanValue()) {
      return priceList.getAllArticles(em);
    }
    Instant expireDate =
        Instant.now().minus(Setting.INVENTORY_INACTIVE_ARTICLE.getIntValue(), ChronoUnit.DAYS);
    return em.createQuery(
            "select a from Article a where a.priceList = :p and a.kbNumber in (select s.kbNumber from ShoppingItem s where s.createDate > :expireDate)",
            Article.class)
        .setParameter("p", priceList)
        .setParameter("expireDate", expireDate)
        .getResultList();
  }

  public static Article getCustomArticleVersion(Consumer<Article> transformer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Article article =
        Tools.optional(
                em.createQuery("select a from Article a where a.kbNumber =:kb", Article.class)
                    .setParameter("kb", ArticleConstants.CUSTOM_PRODUCT.getUniqueIdentifier()))
            .orElseGet(
                () -> {
                  Article out = new Article();
                  Access.putException(out, AccessManager.NO_ACCESS_CHECKING);
                  out.setKbNumber(ArticleConstants.CUSTOM_PRODUCT.getUniqueIdentifier());
                  out.setSupplier(Supplier.getCustomProductSupplier());
                  out.setSurchargeGroup(out.getSupplier().getOrPersistDefaultSurchargeGroup());
                  out.setSuppliersItemNumber(ArticleConstants.CUSTOM_PRODUCT.getUniqueIdentifier());
                  Access.removeException(out);
                  return out;
                });
    Access.runWithAccessManager(
        AccessManager.NO_ACCESS_CHECKING, () -> transformer.accept(article));
    em.persist(article);
    return article;
  }

  public static int getSafeAmount(Article article) {
    int amount = article.getAmount();
    if (amount == 0) {
      return 1;
    }
    return Math.abs(amount);
  }

  public static MetricUnits getMultiplierUnit(Article article) {
    if (article.isWeighable()) {
      return article.getMetricUnits();
    } else {
      return MetricUnits.PIECE;
    }
  }

  public static MetricUnits getContainerUnits(Article article) {
    MetricUnits unit = article.getMetricUnits();
    if (!article.isWeighable() && unit != MetricUnits.NONE) {
      return MetricUnits.PIECE;
    }
    return unit;
  }

  public static MetricUnits getPriceUnit(Article article) {
    if (article.isWeighable()) {
      return article.getMetricUnits().getDisplayUnit();
    } else {
      return MetricUnits.PIECE;
    }
  }

  public static String getPieceAmount(Article article) {
    return String.format("%,1d", getSafeAmount(article)) + article.getMetricUnits().getShortName();
  }

  public static String getContentAmount(Article article) {
    String containerInfo = new DecimalFormat("0.###").format(article.getContainerSize());
    if (article.isWeighable()) {
      return containerInfo + " " + article.getMetricUnits().getDisplayUnit().getShortName();
    } else if (article.getMetricUnits() == MetricUnits.NONE
        || article.getMetricUnits() == MetricUnits.PIECE
        || !(article.getAmount() > 0)) {
      return containerInfo;
    } else {
      return containerInfo
          + " x "
          + article.getAmount()
          + " "
          + article.getMetricUnits().getShortName();
    }
  }
}
