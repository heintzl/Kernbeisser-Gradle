package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import java.util.function.Consumer;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.IModel;

public class ArticleSelectorModel implements IModel<ArticleSelectorController> {
  private final Consumer<Article> consumer;

  ArticleSelectorModel(Consumer<Article> consumer) {
    this.consumer = consumer;
  }

  public Consumer<Article> getConsumer() {
    return consumer;
  }
}
