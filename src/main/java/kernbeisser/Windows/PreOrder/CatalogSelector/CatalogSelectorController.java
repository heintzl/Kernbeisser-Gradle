package kernbeisser.Windows.PreOrder.CatalogSelector;

import java.awt.*;
import java.util.function.Consumer;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.Filters.CatalogFilter;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CatalogSelectorController
    extends Controller<CatalogSelectorView, CatalogSelectorModel> {

  @Linked private final SearchBoxController<CatalogEntry> searchBoxController;
  private final CatalogFilter catalogFilter = new CatalogFilter(this::refreshSearch);

  public CatalogSelectorController(Consumer<CatalogEntry> consumer) {
    super(new CatalogSelectorModel(consumer));
    searchBoxController =
        new SearchBoxController<>(
            (String s, int max) -> model.searchable(s, max, catalogFilter::matches),
            Columns.create("Name", CatalogEntry::getBezeichnung),
            Columns.create("Hersteller", CatalogEntry::getHersteller),
            Columns.create("Barcode", CatalogEntry::getEanLadenEinheit),
            Columns.create("KB-Nummer", CatalogEntry::getArtikelNr),
            Columns.create("Aktion", CatalogEntry::isAction));
    searchBoxController.addDoubleClickListener(e -> this.choose());
    searchBoxController.addExtraComponents(catalogFilter.createFilterUIComponents());
  }

  void refreshSearch() {
    searchBoxController.invokeSearch();
  }

  public void choose() {
    model.getConsumer().accept(searchBoxController.getSelectedObject().orElse(null));
    getView().back();
  }

  public void modifyNamedComponent(String name, Consumer<Component> modifier) {
    searchBoxController.modifyNamedComponent(name, modifier);
  }

  @Override
  public @NotNull CatalogSelectorModel getModel() {
    return model;
  }

  @Override
  public void fillView(CatalogSelectorView catalogSelectorView) {}

  public SearchBoxView<CatalogEntry> getSearchBoxView() {
    return searchBoxController.getView();
  }
}
