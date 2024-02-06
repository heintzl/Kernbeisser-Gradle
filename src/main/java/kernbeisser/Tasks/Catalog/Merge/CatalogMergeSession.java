package kernbeisser.Tasks.Catalog.Merge;

import static kernbeisser.Tasks.Catalog.Catalog.parseArticle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.IgnoredDifference;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Tasks.Catalog.CatalogImporter;
import lombok.Getter;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

public class CatalogMergeSession {

  private static final int FILL_OFFSET = 50000;

  private static final MappedDifference[] MAPPED_DIFFERENCES = MappedDifference.values();

  private final EntityManager em = DBConnection.getEntityManager();
  private final EntityTransaction et = em.getTransaction();

  {
    et.begin();
  }

  @Getter private final Collection<ArticleMerge> articleMerges;

  public CatalogMergeSession(Collection<String> catalogSource) {
    articleMerges = loadSource(catalogSource);
  }

  private final Collection<IgnoredDifference> inserted = new ArrayList<>();

  private HashMap<Integer, Collection<IgnoredDifference>> loadIgnoreDifferences() {
    Collection<IgnoredDifference> queryResult =
        em.createQuery("select i from IgnoredDifference i", IgnoredDifference.class)
            .getResultList();
    HashMap<Integer, Collection<IgnoredDifference>> out = new HashMap<>(queryResult.size());
    queryResult.forEach(
        e -> out.computeIfAbsent(e.getArticle().getSuppliersItemNumber(), b -> new ArrayList<>()));
    queryResult.forEach(e -> out.get(e.getArticle().getSuppliersItemNumber()).add(e));
    return out;
  }

  private HashMap<Integer, Article> loadCurrentState() {
    List<Article> result =
        em.createQuery("select a from Article a where supplier = :s", Article.class)
            .setParameter("s", Supplier.getKKSupplier())
            .getResultList();
    HashMap<Integer, Article> out = new HashMap<>(result.size());
    result.forEach(a -> out.put(a.getSuppliersItemNumber(), a));
    return out;
  }

  private HashMap<Integer, Double> createDepositHashMap(Collection<String> source) {
    HashMap<Integer, Double> deposit = new HashMap<>(10000);
    source.stream()
        .skip(1)
        .map(e -> e.split(";"))
        .forEach(
            e -> {
              try {
                deposit.put(Integer.parseInt(e[0]), Double.parseDouble(e[37].replace(",", ".")));
              } catch (NumberFormatException ignored) {
              }
            });
    return deposit;
  }

  private Stream<CatalogEntry> readSource(Collection<String> source) {
    return source.stream()
        .skip(1)
        .map(e -> e.split(";"))
        .filter(e -> e.length >= 42)
        .map(CatalogImporter::parseRow);
  }

  private Collection<ArticleMerge> loadSource(Collection<String> source) {
    Supplier kkSupplier = Supplier.getKKSupplier();

    UniqueValidator<CatalogEntry> uniqueValidator =
        new UniqueValidator<>(
            UniqueValidator.forbidNull(CatalogEntry::getArtikelNr),
            UniqueValidator.allowNull(
                CatalogEntry::getEanLadenEinheit,
                em.createQuery(
                        "select a.barcode from Article a where a.supplier <> :s and a.barcode is not null",
                        Long.class)
                    .setParameter("s", kkSupplier)
                    .getResultList()));

    return mapToArticleMerge(
            readSource(source).filter(e -> !uniqueValidator.brakesUniqueConstraints(e)),
            createDepositHashMap(source),
            kkSupplier)
        .collect(Collectors.toList());
  }

  public Stream<ArticleMerge> mapToArticleMerge(
      Stream<CatalogEntry> catalogEntryStream, Map<Integer, Double> deposit, Supplier kkSupplier) {
    SurchargeGroup kkDefaultSurchargeGroup = kkSupplier.getOrPersistDefaultSurchargeGroup();
    PriceList coveredIntake = PriceList.getCoveredIntakePriceList();
    Map<Integer, Article> currentState = loadCurrentState();
    Map<Integer, Collection<IgnoredDifference>> currentIgnoredDifferences = loadIgnoreDifferences();
    return catalogEntryStream.map(
        catalogEntry -> {
          Optional<Article> revisionSearch =
              Optional.ofNullable(currentState.get(catalogEntry.getArtikelNrInt()));
          Article newVersion =
              parseArticle(
                  revisionSearch
                      .map(Article::clone)
                      .orElseGet(
                          () -> createNewBase(kkSupplier, coveredIntake, kkDefaultSurchargeGroup)),
                  deposit,
                  kkSupplier,
                  catalogEntry);
          if (!revisionSearch.isPresent()) {
            return new ArticleMerge(
                    newVersion, newVersion, MergeStatus.ADDED, Collections.emptyList())
                .resolved();
          }
          Article revision = revisionSearch.get();
          if (revision.getShopRange() == ShopRange.NOT_IN_RANGE)
            return new ArticleMerge(
                    revision, newVersion, MergeStatus.NO_CONFLICTS, Collections.emptyList())
                .resolved();
          if (isArticleBaseChange(revision, newVersion)) {
            return new ArticleMerge(
                    revision, newVersion, MergeStatus.BASE_CHANGE, Collections.emptyList())
                .resolved();
          }
          Collection<IgnoredDifference> ignoredDifferences =
              currentIgnoredDifferences.get(newVersion.getSuppliersItemNumber());
          return ArticleMerge.updateMerge(
              revision,
              newVersion,
              loadDiffs(
                  revision,
                  newVersion,
                  ignoredDifferences == null ? Collections.emptyList() : ignoredDifferences));
        });
  }

  public boolean isArticleBaseChange(@NotNull Article a, @NotNull Article b) {
    return !Objects.equals(a.getBarcode(), b.getBarcode());
  }

  public void checkMergeStatus(ArticleMerge articleMerge) {
    if (!articleMerge.isResolved()) {
      rollback();
      throw new UnsupportedOperationException("Solve all diffs first");
    }
  }

  private void rollback() {
    et.rollback();
    et.begin();
  }

  public void pushToDB() {
    pushToDB(articleMerges.iterator(), true);
  }

  private void applySolutions(ArticleMerge articleMerge) {
    for (ArticleDifference<?> articleDifference : articleMerge.getArticleDifferences()) {
      switch (articleDifference.getSolution()) {
        case KEEP_AND_IGNORE:
          inserted.add(IgnoredDifference.from(articleMerge.getNewState(), articleDifference));
        case KEEP:
          articleDifference
              .getArticleDifference()
              .transfer(articleMerge.getRevision(), articleMerge.getNewState());
          break;
        case UPDATE:
          break;
        default:
        case NO_SOLUTION:
          rollback();
          throw new UnsupportedOperationException("Solve all diffs first");
      }
    }
  }

  private void markWithNewKBNumber(
      ArticleMerge articleMerge, UniqueNumberIncrementingFactory factory) {
    articleMerge.getNewState().setKbNumber(factory.reserveNextFreeNumber());
  }

  public void pushToDB(Iterator<ArticleMerge> articleMerges, boolean checkVerified) {
    Session session = em.unwrap(Session.class);
    HashSet<Long> barcodes =
        new HashSet<>(
            em.createQuery("select a.barcode from Article a", Long.class).getResultList());
    UniqueNumberIncrementingFactory kbNumberFactory =
        new UniqueNumberIncrementingFactory(
            new HashSet<>(
                em.createQuery("select a.kbNumber" + " from Article a", Integer.class)
                    .getResultList()),
            FILL_OFFSET);
    articleMerges.forEachRemaining(
        articleMerge -> {
          if (checkVerified) checkMergeStatus(articleMerge);
          applySolutions(articleMerge);
          if (!barcodes.add(articleMerge.getNewState().getBarcode()))
            articleMerge.getNewState().setBarcode(null);
          switch (articleMerge.getMergeStatus()) {
            case ADDED:
              markWithNewKBNumber(articleMerge, kbNumberFactory);
              em.persist(articleMerge.getNewState());
              break;
            case BASE_CHANGE:
              markWithNewKBNumber(articleMerge, kbNumberFactory);
            case CONFLICT:
              session.merge(articleMerge.getNewState());
              break;
            case NO_CONFLICTS:
            default:
              break;
          }
        });
    em.flush();
    for (IgnoredDifference ignoredDifference : inserted) {
      em.persist(ignoredDifference);
    }
    em.flush();
    et.commit();
    em.close();
  }

  public void pushAllNotImportantChanges() {
    pushToDB(
        articleMerges.stream()
            .filter(
                e ->
                    e.getMergeStatus() == MergeStatus.ADDED
                        || e.getMergeStatus() == MergeStatus.NO_CONFLICTS)
            .iterator(),
        false);
  }

  private Article createNewBase(Supplier supplier, PriceList priceList, SurchargeGroup sg) {
    Article out = new Article();
    out.setVerified(false);
    out.setSupplier(supplier);
    out.setWeighable(false);
    out.setSurchargeGroup(sg);
    out.setPriceList(priceList);
    out.setShopRange(ShopRange.NOT_IN_RANGE);
    return out;
  }

  public void checkMergeStatus() {
    if (!articleMerges.stream().allMatch(ArticleMerge::isResolved)) {
      throw new UnsupportedOperationException("there are still merge conflicts to resolve");
    }
  }

  public @NotNull Collection<ArticleDifference<?>> loadDiffs(
      @NotNull Article revision,
      @NotNull Article newState,
      @NotNull Collection<IgnoredDifference> ignoredDifferences) {
    Collection<ArticleDifference<?>> differences = new ArrayList<>();
    for (MappedDifference mappedDiff : MAPPED_DIFFERENCES) {
      Object revisionPropertyValue = mappedDiff.get(revision);
      Object newPropertyValue = mappedDiff.get(newState);

      if (Objects.equals(revisionPropertyValue, newPropertyValue)) continue;
      ArticleDifference<?> difference =
          new ArticleDifference<>(mappedDiff, revisionPropertyValue, newPropertyValue);
      if (ignoredDifferences.contains(IgnoredDifference.from(newState, difference))) continue;
      differences.add(difference);
    }
    return differences;
  }

  public void kill() {
    et.rollback();
    em.close();
  }
}
