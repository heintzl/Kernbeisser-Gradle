package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ArticleSelectorController implements Controller {
    private final ArticleSelectorModel model;
    private ArticleSelectorView view;

    private final SearchBoxController<Article> searchBoxController;

    public ArticleSelectorController(Window current, Consumer<Article> consumer){
        view = null;
        searchBoxController = new SearchBoxController<>((s, m) -> {
            Collection<Article> articles = Article.defaultSearch(s, m);
            if (view != null && view.searchOnlyWithoutBarcode()) {
                articles.removeIf(e -> e.getBarcode() != null);
            }
            return articles;
        }, consumer,
                                                        Column.create("Name", Article::getName, Key.ARTICLE_NAME_READ),
                                                        Column.create("Barcode", Article::getBarcode,
                                                                      Key.ARTICLE_BARCODE_READ),
                                                        Column.create("Lieferant", e -> e.getSupplier().getShortName(),
                                                                      Key.ARTICLE_SUPPLIER_READ,
                                                                      Key.SUPPLIER_SHORT_NAME_READ));
        this.model = new ArticleSelectorModel(consumer);
        this.view = new ArticleSelectorView(current,this);
    }

    public void choose() {
        model.getConsumer().accept(searchBoxController.getSelectedObject());
        view.back();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public SearchBoxView<Article> getSearchBoxView() {
        return searchBoxController.getView();
    }

    public void refreshLoadSolutions() {
        searchBoxController.refreshLoadSolutions();
    }
}
