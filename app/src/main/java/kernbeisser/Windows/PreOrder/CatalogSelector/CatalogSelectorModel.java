package kernbeisser.Windows.PreOrder.CatalogSelector;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Useful.OptionalPredicate;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class CatalogSelectorModel
    implements IModel<CatalogSelectorController>, OptionalPredicate<CatalogEntry> {
  @Getter private final Consumer<CatalogEntry> consumer;
  private final List<CatalogEntry> catalog;
  private final Map<Integer, Boolean> articleKKNumberOffers =
      ArticleRepository.kkItemNumberOffersFromArticles();

  CatalogSelectorModel(Consumer<CatalogEntry> consumer) {
    this.consumer = consumer;
    this.catalog =
        DBConnection.getAll(CatalogEntry.class).stream()
            .filter(e -> !e.isOutdatedOffer())
            .collect(Collectors.toList());
  }

  Collection<CatalogEntry> searchable(
      String s, int max, Predicate<CatalogEntry> catalogEntryFilter) {
    return catalog.stream()
        .filter(e -> e.matches(s))
        .filter(catalogEntryFilter)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Boolean> optionalTrue(CatalogEntry entry) {
    try {
      Boolean offer = articleKKNumberOffers.get(entry.getArtikelNrInt());
      return Optional.ofNullable(offer);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
