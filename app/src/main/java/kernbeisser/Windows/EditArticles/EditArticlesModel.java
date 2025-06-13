package kernbeisser.Windows.EditArticles;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Enums.ArticleDeletionResult;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Tasks.Catalog.Catalog;
import kernbeisser.Useful.Constants;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class EditArticlesModel implements IModel<EditArticlesController> {

  @Getter private List<ArticleComparedToCatalogEntry> differences;
  private final Map<Integer, Instant> supplierOfferNumbersValidFromMap =
      Catalog.supplierOfferNumbersValidFromMap();

  public void previewCatalog(Collection<Article> articles) {
    differences = ArticleRepository.compareArticlesToCatalog(articles, Constants.KK_SUPPLIER);
  }

  public List<String> mergeCatalog(Collection<Article> articles) {
    return ArticleRepository.mergeCatalog(articles, differences);
  }

  public Map<ArticleDeletionResult, List<Article>> prepareRemoval(Collection<Article> articles) {
    return ArticleRepository.prepareRemoval(articles);
  }

  public static void remove(Map<ArticleDeletionResult, List<Article>> preparedArticles) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    ArticleRepository.removeArticles(em, preparedArticles.get(ArticleDeletionResult.DELETE));
    ArticleRepository.unlistArticles(em, preparedArticles.get(ArticleDeletionResult.DISCONTINUE));
  }

  @Nullable
  public Instant supplierOfferFrom(Article article) {
    if (!article.getSupplier().equals(Constants.KK_SUPPLIER)) {
      return null;
    }
    return supplierOfferNumbersValidFromMap.get(article.getSuppliersItemNumber());
  }
}
