package kernbeisser.Windows.EditArticles;

import jakarta.persistence.EntityManager;
import java.util.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Enums.ArticleDeletionResult;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class EditArticlesModel implements IModel<EditArticlesController> {

  @Getter private List<ArticleComparedToCatalogEntry> differences;

  public void previewCatalog(Collection<Article> articles) {
    differences = ArticleRepository.compareArticlesToCatalog(articles, Supplier.KK_SUPPLIER);
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
}
