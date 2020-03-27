package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.Model;

import java.util.function.Consumer;

public class ArticleSelectorModel implements Model {
    private final Consumer<Article> consumer;

    ArticleSelectorModel(Consumer<Article> consumer){
        this.consumer = consumer;
    }

    public Consumer<Article> getConsumer() {
        return consumer;
    }
}
