package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import java.util.function.Consumer;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.Filters.ArticleFilter;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ArticleSelectorController
    extends Controller<ArticleSelectorView, ArticleSelectorModel> {

  @Linked private final SearchBoxController<Article> searchBoxController;
  private ArticleFilter articleFilter = new ArticleFilter(this::refreshSearch);

  public ArticleSelectorController(Consumer<Article> consumer) {
    super(new ArticleSelectorModel(consumer));
    searchBoxController =
        new SearchBoxController<>(
            articleFilter::searchable,
            Column.create("Name", Article::getName),
            Column.create("Barcode", Article::getBarcode),
            Column.create("KB-Nummer", Article::getKbNumber),
            Column.create(
                "Lieferant",
                e ->
                    e.getSupplier().getShortName()
                        + (e.getSuppliersItemNumber() > 0
                            ? " (" + e.getSuppliersItemNumber() + ")"
                            : "")));
    searchBoxController.addDoubleClickListener(e -> this.choose());
    searchBoxController.addExtraCheckboxes(articleFilter.createFilterCheckboxes());
  }

  void refreshSearch() {
    searchBoxController.invokeSearch();
  }

  public void choose() {
    model.getConsumer().accept(searchBoxController.getSelectedObject());
    getView().back();
  }

  @Override
  public @NotNull ArticleSelectorModel getModel() {
    return model;
  }

  @Override
  public void fillView(ArticleSelectorView articleSelectorView) {}

  public SearchBoxView<Article> getSearchBoxView() {
    return searchBoxController.getView();
  }
}
