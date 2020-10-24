package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ArticleSelectorController
    implements IController<ArticleSelectorView, ArticleSelectorModel> {
  private final ArticleSelectorModel model;
  private ArticleSelectorView view;

  @Linked private final SearchBoxController<Article> searchBoxController;

  public ArticleSelectorController(Consumer<Article> consumer) {
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
    this.model = new ArticleSelectorModel(consumer);
  }

  private Collection<Article> search(String query, int max) {
    getView();
    return Article.getDefaultAll(
        query, createFilter(view.searchOnlyWithoutBarcode(), view.searchOnlyShowInShop()), max);
  }

  private Predicate<Article> createFilter(boolean filterBarcode, boolean filterShowInShoppingCart) {
    return e ->
        !(filterBarcode && e.getBarcode() != null)
            && !(filterShowInShoppingCart && !e.isShowInShop());
  }

  void refreshSearch() {
    searchBoxController.search();
  }

  public void choose() {
    model.getConsumer().accept(searchBoxController.getSelectedObject());
    view.back();
  }

  @Override
  public @NotNull ArticleSelectorModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

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
