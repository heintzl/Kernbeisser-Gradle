package kernbeisser.Windows.EditCatalog;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Useful.OptionalPredicate;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class EditCatalogModel
    implements IModel<EditCatalogController>, OptionalPredicate<CatalogEntry> {

  @Getter private final Collection<CatalogEntry> catalog;

  private final Map<Integer, Boolean> articleKKNumberOffers =
      ArticleRepository.kkItemNumberOffersFromArticles();

  public EditCatalogModel() {
    catalog = DBConnection.getAll(CatalogEntry.class);
  }

  // Ignores the Max rows setting
  Collection<CatalogEntry> searchable(
      String s, int max, Predicate<CatalogEntry> catalogEntryFilter) {
    return catalog.stream()
        .filter(e -> e.matches(s))
        .filter(catalogEntryFilter)
        .collect(Collectors.toList());
  }

  public Optional<Boolean> isArticleOffer(CatalogEntry entry) {
    try {
      Boolean offer = articleKKNumberOffers.get(entry.getArtikelNrInt());
      return Optional.ofNullable(offer);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public void setOffer(Article article, boolean offer) {
    article.setOffer(offer);
    Tools.merge(article);
    articleKKNumberOffers.replace(article.getSuppliersItemNumber(), offer);
  }

  public Article makeArticle(CatalogEntry entry, boolean offer) {
    Article newArticle = ArticleRepository.createArticleFromCatalogEntry(entry);
    newArticle.setOffer(offer);
    Tools.persist(newArticle);
    articleKKNumberOffers.put(entry.getArtikelNrInt(), offer);
    return newArticle;
  }

  @Override
  public Optional<Boolean> optionalTrue(CatalogEntry entry) {
    return isArticleOffer(entry);
  }
}
