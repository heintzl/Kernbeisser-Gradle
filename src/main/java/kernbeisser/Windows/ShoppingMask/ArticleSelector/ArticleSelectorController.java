package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

public class ArticleSelectorController implements Controller<ArticleSelectorView,ArticleSelectorModel> {
    private final ArticleSelectorModel model;
    private ArticleSelectorView view;

    private final SearchBoxController<Article> searchBoxController;

    public ArticleSelectorController(Consumer<Article> consumer){
        view = null;
        searchBoxController = new SearchBoxController<>((s, m) -> {
            Collection<Article> articles = Article.defaultSearch(s, m);
            if (view != null && view.searchOnlyWithoutBarcode()) {
                articles.removeIf(e -> e.getBarcode() != null);
            }
            return articles;
        },
                                                        Column.create("Name", Article::getName),
                                                        Column.create("Barcode", Article::getBarcode),
                                                        Column.create("KB-Nummer", Article::getKbNumber),
                                                        Column.create("Lieferant", e -> e.getSupplier().getShortName() + (e.getSuppliersItemNumber()>0?" (" +e.getSuppliersItemNumber()+ ")":"")));
        searchBoxController.initView();
        this.model = new ArticleSelectorModel(consumer);
        this.view = new ArticleSelectorView(this);
    }

    public void choose() {
        model.getConsumer().accept(searchBoxController.getSelectedObject());
        view.back();
    }

    @Override
    public @NotNull ArticleSelectorView getView() {
        return view;
    }

    @Override
    public @NotNull ArticleSelectorModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public SearchBoxView<Article> getSearchBoxView() {
        return searchBoxController.getView();
    }

    public void refreshLoadSolutions() {
        searchBoxController.refreshLoadSolutions();
    }
}
