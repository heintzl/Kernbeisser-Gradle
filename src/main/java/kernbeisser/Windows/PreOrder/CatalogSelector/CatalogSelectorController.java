package kernbeisser.Windows.PreOrder.CatalogSelector;

import static kernbeisser.DBEntities.CatalogEntry.CATALOG_ENTRY_STATES;

import java.awt.*;
import java.util.function.Consumer;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.Filters.CatalogFilter;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Useful.Date;
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
            Columns.<CatalogEntry>create(
                    "Status", e -> CATALOG_ENTRY_STATES.get(e.getAenderungskennung()))
                .withDefaultFilter(),
            Columns.create("Name", CatalogEntry::getBezeichnung),
            Columns.create("Gebinde", CatalogEntry::getBestelleinheit),
            Columns.<CatalogEntry>create("Netto-Pr.", e -> String.format("%.2f€", e.getPreis()))
                .withSorter(Column.NUMBER_SORTER),
            Columns.<CatalogEntry>create(
                    "Geb.-Pr.",
                    e -> String.format("%.2f€", e.getPreis() * e.getBestelleinheitsMenge()))
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Hersteller", CatalogEntry::getHersteller).withDefaultFilter(),
            Columns.create("Barcode", CatalogEntry::getEanLadenEinheit),
            Columns.create("KK-Nummer", CatalogEntry::getArtikelNr),
            Columns.<CatalogEntry>create(
                    "Aktion bis",
                    e ->
                        e.isAction()
                            ? Date.INSTANT_DATE.format(e.getAktionspreisGueltigBis())
                            : "-")
                .withDefaultFilter()
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)));
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
