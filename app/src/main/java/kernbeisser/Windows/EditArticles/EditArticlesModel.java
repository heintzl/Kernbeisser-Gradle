package kernbeisser.Windows.EditArticles;

import java.util.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class EditArticlesModel implements IModel<EditArticlesController> {

  @Getter private List<ArticleComparedToCatalogEntry> differences;

  public void previewCatalog(Collection<Article> articles) {
    differences =
        ArticleRepository.compareArticlesToCatalog(articles, ArticleRepository.KK_SUPPLIER);
  }

  public List<String> mergeCatalog(Collection<Article> articles) {
    return ArticleRepository.mergeCatalog(articles, differences);
  }
}
