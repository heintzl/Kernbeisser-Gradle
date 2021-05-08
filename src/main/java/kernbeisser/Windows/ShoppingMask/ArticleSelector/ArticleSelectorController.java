package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ArticleSelectorController
    extends Controller<ArticleSelectorView, ArticleSelectorModel> {

  @Linked private final SearchBoxController<Article> searchBoxController;

  public ArticleSelectorController(Consumer<Article> consumer) {
    super(new ArticleSelectorModel(consumer));
    searchBoxController =
        new SearchBoxController<>(
            this::search,
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
  }

  private Collection<Article> search(String query, int max) {
    ArticleSelectorView view = getView();
    return Article.getDefaultAll(
        query,
        createFilter(
            view.searchOnlyWithoutBarcode(),
            view.searchOnlyShowInShop(),
            view.searchOnlyShopRange()),
        max);
  }

  private Predicate<Article> createFilter(
      boolean filterBarcode, boolean filterShowInShoppingCart, boolean filterShopRange) {
    return e ->
        !(filterBarcode && e.getBarcode() != null)
            && !(filterShowInShoppingCart && !e.isShowInShop())
            && !(filterShopRange && !e.isShopRange());
  }

  void refreshSearch() {
    searchBoxController.invokeSearch();
  }

  public void choose() {
    model.getConsumer().accept(searchBoxController.getSelectedObject().orElse(null));
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
