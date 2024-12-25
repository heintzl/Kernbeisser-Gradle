package kernbeisser.Windows.EditCatalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Article_;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class EditCatalogModel implements IModel<EditCatalogController> {

  @Getter private final Collection<CatalogEntry> catalog;
  @Getter private final List<Integer> articleKKNumbers = ArticleRepository.kkItemNumbersFromArticles();

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

  public boolean isArticle(CatalogEntry entry) {
    try {
      return articleKKNumbers.contains(entry.getArtikelNrInt());
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public void activateAction(Article article) {
    article.setOffer(true);
    Tools.merge(article);
    articleKKNumbers.add(article.getSuppliersItemNumber());
  }

  public Article makeArticle(CatalogEntry entry) {
    Article newArticle = ArticleRepository.createArticleFromCatalogEntry(entry);
    Tools.persist(newArticle);
    articleKKNumbers.add(entry.getArtikelNrInt());
    return newArticle;
  }
}
