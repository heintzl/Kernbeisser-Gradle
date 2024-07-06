package kernbeisser.DBEntities.Repositories;

import static kernbeisser.DBConnection.ExpressionFactory.*;
import static kernbeisser.DBConnection.PredicateFactory.*;
import static kernbeisser.Enums.ArticleDeletionResult.*;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.*;
import kernbeisser.DBEntities.*;
import kernbeisser.EntityWrapper.ObjectState;
import kernbeisser.Enums.*;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import rs.groump.Access;
import rs.groump.AccessManager;

@Log4j2
public class ArticleRepository {
  public static final Supplier KK_SUPPLIER = Supplier.getKKSupplier();

  private ArticleRepository() {}

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
    return (supplier != null && supplier.equals(KK_SUPPLIER));
  }

  public static Optional<ObjectState<Article>> getByKbNumber(
      EntityManager em, int kbNumber, boolean filterShopRange) {
    var qb = QueryBuilder.selectAll(Article.class).where(Article_.kbNumber.eq(kbNumber));
    if (filterShopRange) qb.where(Article_.shopRange.in(ShopRange.visibleRanges()));
    return qb.getResultStream(em)
        .findAny()
        .map(ObjectState::currentState)
        .or(() -> searchInArticleHistoryForKbNumber(em, kbNumber));
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
            AuditReaderFactory.get(em)
                .createQuery()
                .forRevisionsOfEntity(Article.class, false, true)
                .add(AuditEntity.property("kbNumber").eq(kbNumber))
                .addOrder(AuditEntity.property("updateDate").desc())
                .getResultList()
                .stream()
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
    return getBySuppliersItemNumber(KK_SUPPLIER, suppliersNumber);
  }

  public static Optional<Article> getBySuppliersItemNumber(
      Supplier targetSupplier, int suppliersNumber, EntityManager em) {
    return DBConnection.getConditioned(
            em,
            Article.class,
            Article_.suppliersItemNumber.eq(suppliersNumber),
            Article_.supplier.eq(targetSupplier))
        .stream()
        .findFirst();
  }

  public static Optional<Article> getByBarcode(long targetBarcode) throws NoResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<Article> articles =
        DBConnection.getConditioned(em, Article.class, Article_.barcode.eq(targetBarcode));
    return Optional.ofNullable(
        articles.stream()
            .filter(ArticleRepository::articleIsActiveOffer)
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
      return QueryBuilder.selectAll(Article.class)
          .where(Article_.name.eq(rawPrice.getName()), Article_.kbNumber.in(rawPriceIdentifiers))
          .getSingleResult(em);
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
      rs.groump.Access.runWithAccessManager(
          AccessManager.ACCESS_GRANTED,
          () -> article.setSurchargeGroup(supplier.get().getOrPersistDefaultSurchargeGroup(em)));
      article.setShopRange(ShopRange.NOT_IN_RANGE);
      em.persist(article);
      em.flush();
      et.commit();
      return getOrCreateRawPriceArticle(rawPrice);
    } catch (Exception o) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(o);
    }
  }

  private static TypedQuery<Article> createQuery(EntityManager em, String search) {
    if (search.startsWith("PL:")) {
      return QueryBuilder.selectAll(Article.class)
          .where(Article_.priceList.child(PriceList_.name).eq(search.replace("PL:", "").trim()))
          .buildQuery(em);
    }
    int n = Tools.tryParseInt(search);
    String ds = (search.length() > 3 ? "%" + search + "%" : search + "%").toLowerCase();
    long l = Tools.tryParseLong(search);
    return QueryBuilder.selectAll(Article.class)
        .where(
            or(
                Article_.kbNumber.eq(n),
                Article_.suppliersItemNumber.eq(n),
                like(lower(Article_.name), ds),
                Article_.barcode.eq(l),
                like(Article_.barcode.as(String.class), "%" + search)))
        .orderBy(Article_.name.asc())
        .buildQuery(em);
  }

  // TODO find better solution this one only makes sense when trying to find hundreds of free ids...
  public static int nextFreeKBNumber(EntityManager em, int min) {
    List<Integer> usedKbNumbers =
        QueryBuilder.select(Article_.kbNumber)
            .where(greaterOrEq(Article_.kbNumber, asExpression(min)))
            .orderBy(Article_.kbNumber.asc())
            .getResultList(em);
    for (int id = min; ; id++) {
      if (!usedKbNumbers.contains(id)) return id;
    }
  }

  public static int nextFreeKBNumber(EntityManager em) {
    return nextFreeKBNumber(em, 1);
  }

  public static Article nextArticleTo(
      EntityManager em, int suppliersItemNumber, Supplier supplier) {
    return QueryBuilder.selectAll(Article.class)
        .where(Article_.supplier.eq(supplier))
        .orderBy(diff(Article_.suppliersItemNumber, asExpression(suppliersItemNumber)).asc())
        .buildQuery(em)
        .setMaxResults(1)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }

  public static Article nextArticleTo(
      EntityManager em, int suppliersItemNumber, Supplier supplier, PriceList excludedPriceList) {
    return QueryBuilder.selectAll(Article.class)
        .where(Article_.supplier.eq(supplier), Article_.priceList.eq(excludedPriceList).not())
        .orderBy(diff(Article_.suppliersItemNumber, asExpression(suppliersItemNumber)).asc())
        .getResultStream(em)
        .findFirst()
        .orElse(null);
  }

  public static boolean articleIsActiveOffer(Article article) {
    return article.isOffer() && findOfferOn(article).isPresent();
  }

  public static Optional<Offer> findOfferOn(Article article) {
    return QueryBuilder.selectAll(Offer.class)
        .where(Offer_.offerArticle.eq(article))
        .getResultList()
        .stream()
        .filter(e -> e.getFromDate().isBefore(Instant.now()))
        .filter(e -> e.getToDate().isAfter(Instant.now()))
        .findAny();
  }

  public static Map<Integer, Instant> getLastDeliveries() {
    return QueryBuilder.select(
            ShoppingItem.class, ShoppingItem_.kbNumber, max(ShoppingItem_.createDate))
        .where(ShoppingItem_.purchase.isNull())
        .groupBy(ShoppingItem_.kbNumber)
        .orderBy(ShoppingItem_.kbNumber.asc())
        .getResultList()
        .stream()
        .collect(
            Collectors.toMap(
                tuple -> tuple.get(0, Integer.class), tuple -> tuple.get(1, Instant.class)));
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

  public static Collection<Article> getPrintPool() {
    return QueryBuilder.select(ArticlePrintPool_.article).distinct().getResultList();
  }

  public static int getArticlePrintPoolSize() {
    return QueryBuilder.select(ArticlePrintPool.class, sum(ArticlePrintPool_.number))
        .getSingleResultOptional()
        .map(tuple -> tuple.get(0, Integer.class))
        .orElse(0);
  }

  public static Collection<Article> getAllActiveArticlesFromPriceList(
      EntityManager em, PriceList priceList) {
    if (!Setting.INVENTORY_LOOK_FOR_ARTICLE_USAGE.getBooleanValue()) {
      return priceList.getAllArticles(em);
    }
    Instant expireDate =
        Instant.now().minus(Setting.INVENTORY_INACTIVE_ARTICLE.getIntValue(), ChronoUnit.DAYS);
    List<Integer> articleIds =
        QueryBuilder.select(ShoppingItem_.articleId)
            .where(greaterOrEq(ShoppingItem_.createDate, asExpression(expireDate)))
            .distinct()
            .getResultStream(em)
            .map(Long::intValue)
            .toList();
    return QueryBuilder.selectAll(Article.class)
        .where(Article_.priceList.eq(priceList), Article_.id.in(articleIds))
        .getResultList(em);
  }

  public static Article getCustomArticleVersion(Consumer<Article> transformer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return Access.runWithAccessManager(
        AccessManager.ACCESS_GRANTED,
        () -> {
          Article article =
              DBConnection.getConditioned(
                      em,
                      Article.class,
                      Article_.kbNumber.eq(ArticleConstants.CUSTOM_PRODUCT.getUniqueIdentifier()))
                  .stream()
                  .findFirst()
                  .orElseGet(ArticleRepository::createCustomArticlePreset);
          Access.runWithAccessManager(
              AccessManager.ACCESS_GRANTED, () -> transformer.accept(article));
          em.persist(article);
          return article;
        });
  }

  private static Article createCustomArticlePreset() {
    Article out = new Article();
    out.setKbNumber(ArticleConstants.CUSTOM_PRODUCT.getUniqueIdentifier());
    out.setSupplier(Supplier.getCustomProductSupplier());
    out.setSurchargeGroup(out.getSupplier().getOrPersistDefaultSurchargeGroup());
    out.setSuppliersItemNumber(ArticleConstants.CUSTOM_PRODUCT.getUniqueIdentifier());
    return out;
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

  public static PriceList getValidPriceList(EntityManager em, Article article) {
    PriceList priceList = article.getPriceList();
    if (!priceList.getName().equals("Verdeckte Aufnahme")) return priceList;
    return nextArticleTo(em, article.getSuppliersItemNumber(), article.getSupplier(), priceList)
        .getPriceList();
  }

  public static boolean validateBarcode(Long barcode) {
    return barcode > 1E9;
  }

  public static Map<String, CatalogEntry> getArticleCatalogMap(
      Collection<Article> articles, Supplier supplier) {
    Collection<String> articleNos =
        articles.stream()
            .filter(e -> e.getSupplier().equals(supplier))
            .mapToInt(Article::getSuppliersItemNumber)
            .mapToObj(Integer::toString)
            .collect(Collectors.toList());
    return QueryBuilder.selectAll(CatalogEntry.class)
        .where(
            CatalogEntry_.aktionspreis.eq(false),
            CatalogEntry_.artikelNr.in(articleNos),
            or(
                CatalogEntry_.ladeneinheit.isNull().not(),
                CatalogEntry_.gebindePfand.eq(0.0).not(),
                CatalogEntry_.einzelPfand.eq(0.0).not()))
        .getResultList()
        .stream()
        .collect(Collectors.toMap(CatalogEntry::getArtikelNr, c -> c));
  }

  public static List<ArticleComparedToCatalogEntry> compareArticlesToCatalog(
      Collection<Article> articles, Supplier supplier) {
    List<ArticleComparedToCatalogEntry> differences = new ArrayList<>();
    Map<String, CatalogEntry> catalogMap = getArticleCatalogMap(articles, supplier);
    for (Article article : articles) {
      if (!article.getSupplier().equals(supplier)) {
        continue;
      }
      CatalogEntry correspondingCatalogEntry =
          catalogMap.get(Integer.toString(article.getSuppliersItemNumber()));
      if (correspondingCatalogEntry == null) {
        continue;
      }
      ArticleComparedToCatalogEntry compared =
          new ArticleComparedToCatalogEntry(article, correspondingCatalogEntry);
      String description = "";
      switch (compared.getResultType()) {
        case BARCODE_CHANGED:
          description = "Barcode geändert";
          break;
        case BARCODE_CONFLICT_SAME_SUPPLIER:
          description = "Barcode veraltet";
          break;
        case BARCODE_CONFLICT_OTHER_SUPPLIER:
          description = "Barcode (Lieferanten-Konflikt)";
          break;
      }
      Set<String> fieldDifferences = compared.getFieldDifferences();
      if (!description.isEmpty()) {
        fieldDifferences.remove("Barcode");
        description += ", ";
      }
      compared.setDescription(description + String.join(", ", fieldDifferences));
      differences.add(compared);
    }
    return differences;
  }

  public static List<String> mergeCatalog(
      Collection<Article> articles, List<ArticleComparedToCatalogEntry> differences) {
    List<String> mergeLog = new ArrayList<>();
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();

    try {
      String logBuilder;
      for (ArticleComparedToCatalogEntry difference : differences) {
        logBuilder = "";
        Article article = difference.getArticle();
        if (!articles.contains(article)) {
          continue;
        }

        CatalogEntry catalogEntry = difference.getCatalogEntry();
        ArticleCatalogState resultType = difference.getResultType();

        logBuilder += "Artikel " + article.getSuppliersItemNumber() + ":";
        long a_barcode = Tools.ifNull(article.getBarcode(), -999L);
        long c_barcode = Tools.ifNull(catalogEntry.getEanLadenEinheit(), -999L);
        double singleDeposit = catalogEntry.getEinzelPfand();
        double containerDeposit = catalogEntry.getGebindePfand();
        boolean changed = false;

        switch (resultType) {
          case EQUAL:
            break;
          case DIFFERENT:
            if (singleDeposit > 0.0) {
              logBuilder +=
                  String.format(
                      " E.-Pf. %.2f -> %.2f |", article.getSingleDeposit(), singleDeposit);
              article.setSingleDeposit(singleDeposit);
              changed = true;
            }
            if (containerDeposit > 0.0) {
              logBuilder +=
                  String.format(
                      " G.-Pf. %.2f -> %.2f |", article.getContainerDeposit(), containerDeposit);
              article.setContainerDeposit(containerDeposit);
              changed = true;
            }
            if (validateBarcode(c_barcode) && c_barcode != a_barcode) {
              logBuilder += String.format(" bc %s -> %s |", article.getBarcode(), c_barcode);
              article.setBarcode(c_barcode);
              changed = true;
            }
            break;
          case BARCODE_CHANGED:
            logBuilder +=
                " Neuer Barcode. Möglicherweise ist die Lieferanten-Artikelnummer neu vergeben worden?";
            break;
          case BARCODE_CONFLICT_SAME_SUPPLIER:
            Article conflictingArticle = difference.getConflictingArticle();
            logBuilder +=
                String.format(
                    " Derselbe Barcode,ist bereits bei dem %s-Artikel mit der KB-Artikelnummer %d vergeben. Ist einer der Artikel veraltet?",
                    conflictingArticle.getSupplier().getShortName(),
                    conflictingArticle.getKbNumber());
            break;
          case BARCODE_CONFLICT_OTHER_SUPPLIER:
            conflictingArticle = difference.getConflictingArticle();
            logBuilder +=
                String.format(
                    " Derselbe Barcode,ist bereits bei dem %s-Artikel mit der KB-Artikelnummer %d vergeben. Der Barcode sollte nur bei einem Lieferanten vergeben sein.",
                    conflictingArticle.getSupplier().getShortName(),
                    conflictingArticle.getKbNumber());
        }
        if (changed) {
          em.merge(article);
        }
        log.info(logBuilder);
        mergeLog.add(logBuilder);
      }

      em.flush();
      et.commit();
      return mergeLog;

    } catch (Exception e) {
      UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
      // TODO() code below this will never get called ^ throws
      et.rollback();
      log.error("Rolled back changes due to an exception");
      mergeLog.add(
          "Übernahme wurde mit Fehlern abgebrochen. Alle Änderungen wurden Rückgängig gemacht");
      return mergeLog;
    }
  }

  public static List<Article> getArticlesStateAtDate(Date date, List<Integer> articleIds) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();

    if (articleIds.isEmpty()) {
      return Collections.emptyList();
    }
    AuditReader reader = AuditReaderFactory.get(em);
    Number revision = reader.getRevisionNumberForDate(date);
    AuditQuery query = reader.createQuery().forEntitiesAtRevision(Article.class, revision);
    if (articleIds != null) {
      query = query.add(AuditEntity.property("id").in(articleIds));
    }

    return query.getResultList();
  }

  public static Article getArticleStateAtDate(Article article, Instant instant) {
    Date date = Date.from(instant);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return AuditReaderFactory.get(em).find(Article.class, article.getId(), date);
  }

  public static Map<ArticleDeletionResult, List<Article>> prepareRemoval(
      Collection<Article> articles) {

    Map<ArticleDeletionResult, List<Article>> result = new HashMap<>();
    result.put(PREORDERED, new ArrayList<>());
    result.put(RECENTLY_TRADED, new ArrayList<>());
    result.put(RECENT_INVENTORY, new ArrayList<>());
    result.put(DISCONTINUE, new ArrayList<>());
    result.put(DELETE, new ArrayList<>());

    @Cleanup EntityManager em = DBConnection.getEntityManager();

    Collection<String> preorderedSupplierNumbers =
        QueryBuilder.select(PreOrder_.catalogEntry.child(CatalogEntry_.artikelNr))
            .where(
                and(
                    PreOrder_.delivery.isNull(),
                    PreOrder_.catalogEntry
                        .child(CatalogEntry_.artikelNr)
                        .in(
                            articles.stream()
                                .filter(a -> a.getSupplier().equals(Supplier.getKKSupplier()))
                                .map(a -> Integer.toString(a.getSuppliersItemNumber()))
                                .toList())))
            .getResultList();

    Map<Integer, LocalDate> articleNumbersLastInventory =
        QueryBuilder.selectAll(ArticleStock.class)
            .where(ArticleStock_.article.in(articles))
            .getResultStream(em)
            .collect(
                Collectors.toMap(
                    s -> s.getArticle().getKbNumber(),
                    ArticleStock::getInventoryDate,
                    (s1, s2) -> s1.isBefore(s2) ? s2 : s1));

    em.clear();

    Instant inactivityThreshold =
        Instant.now().minus(Setting.INVENTORY_INACTIVE_ARTICLE.getIntValue(), ChronoUnit.DAYS);
    Collection<Integer> recentlyTradedArticleNumbers =
        QueryBuilder.select(ShoppingItem_.kbNumber)
            .where(
                and(
                    ShoppingItem_.kbNumber.in(
                        articles.stream().map(Article::getKbNumber).toList())),
                or(
                    greaterOrEq(ShoppingItem_.createDate, asExpression(inactivityThreshold)),
                    greaterOrEq(
                        ShoppingItem_.purchase.child(Purchase_.createDate),
                        asExpression(inactivityThreshold))))
            .getResultList();

    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Article article : articles) {
      ArticleDeletionResult deletionResult;
      Integer kbNumber = article.getKbNumber();
      LocalDate lastInventory = articleNumbersLastInventory.get(kbNumber);
      if (preorderedSupplierNumbers.contains(Integer.toString(article.getSuppliersItemNumber()))) {
        deletionResult = PREORDERED;
      } else if (recentlyTradedArticleNumbers.contains(kbNumber)) {
        deletionResult = RECENTLY_TRADED;
      } else if (lastInventory != null) {
        if (lastInventory.isBefore(
            LocalDate.ofInstant(inactivityThreshold, ZoneId.systemDefault()))) {
          deletionResult = DISCONTINUE;
        } else {
          deletionResult = RECENT_INVENTORY;
        }
      } else {
        deletionResult = DELETE;
      }
      result.get(deletionResult).add(article);
    }
    return result;
  }

  private static void cleanupArticleReferences(EntityManager em, Collection<Article> articles) {

    QueryBuilder.selectAll(Shelf.class)
        .getResultStream(em)
        .forEach(
            s -> {
              Shelf dbShelf = em.find(Shelf.class, s.getId());
              articles.forEach(dbShelf.getArticles()::remove);
              em.merge(dbShelf);
            });

    em.createQuery("DELETE FROM ArticlePrintPool p WHERE p.article in (:a)")
        .setParameter("a", articles)
        .executeUpdate();
  }

  public static void unlistArticles(EntityManager em, Collection<Article> articles) {
    cleanupArticleReferences(em, articles);
    for (Article a : articles) {
      Article dbArticle = em.find(Article.class, a.getId());
      if (a.getKbNumber() >= 0) {
        int newKbNumber = -100000 - dbArticle.getKbNumber();
        while (getByKbNumber(newKbNumber, false).isPresent()) newKbNumber -= 10000;
        dbArticle.setKbNumber(newKbNumber);
      }
      dbArticle.setShopRange(ShopRange.DISCONTINUED);
      em.merge(dbArticle);
    }
  }

  public static void removeArticles(EntityManager em, Collection<Article> articles) {
    cleanupArticleReferences(em, articles);
    em.createQuery("DELETE FROM PreOrder p WHERE p.article in (:a) AND p.delivery IS NOT NULL")
        .setParameter("a", articles)
        .executeUpdate();
    em.createQuery("DELETE FROM Article a WHERE a in (:a)")
        .setParameter("a", articles)
        .executeUpdate();
  }
}
