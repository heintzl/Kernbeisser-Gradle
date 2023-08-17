package kernbeisser.Windows.EditCatalog;

import com.google.common.collect.ImmutableMap;
import java.awt.event.KeyEvent;
import java.util.List;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.Filters.CatalogFilter;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormImplemetations.CatalogEntry.CatalogEntryController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Icons;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class EditCatalogController extends Controller<EditCatalogView, EditCatalogModel> {

  private final ObjectViewController<CatalogEntry> objectViewController;

  private final BarcodeCapture capture;

  private final CatalogFilter catalogFilter = new CatalogFilter(this::refreshList);

  private static final ImmutableMap<String, String> catalogEntryStates =
      ImmutableMap.<String, String>builder()
          .put("A", "Änderung")
          .put("N", "Neu")
          .put("R", "Restbestand")
          .put("V", "Vorübergehend ausgelistet")
          .put("W", "Eiedergelistet")
          .put("X", "Ausgelistet")
          .build();

  private Double getDepositPrice(List<CatalogEntry> c) {
    if (c.isEmpty()) {
      return 0.0;
    }
    ;
    CatalogEntry c0 = c.get(0);
    return c0.getPreis();
  }

  @Key(PermissionKey.ACTION_LOGIN) // TODO add more restrictions, if required
  public EditCatalogController() {
    super(new EditCatalogModel());

    objectViewController =
        new ObjectViewController<>(
            "Katalog",
            new CatalogEntryController(),
            catalogFilter::searchable,
            false,
            Columns.<CatalogEntry>create(
                    "Status", e -> catalogEntryStates.get(e.getAenderungskennung()))
                .withDefaultFilter()
                .withPreferredWidth(80),
            Columns.create("Artikelnummer", CatalogEntry::getArtikelNr)
                .withDefaultFilter()
                .withPreferredWidth(80)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Bezeichnung", CatalogEntry::getBezeichnung)
                .withDefaultFilter()
                .withPreferredWidth(250),
            Columns.create("Hersteller", CatalogEntry::getMarke)
                .withDefaultFilter()
                .withPreferredWidth(120),
            Columns.create("Herkunft", CatalogEntry::getHerkunft)
                .withDefaultFilter()
                .withPreferredWidth(80),
            Columns.<CatalogEntry>create("Preis", e -> String.format("%.2f€", e.getPreis()))
                .withPreferredWidth(60),
            Columns.<CatalogEntry>createIconColumn(
                    "Aktion", e -> Icons.booleanIcon(e.getAktionspreis()))
                .withPreferredWidth(50),
            Columns.<CatalogEntry>create(
                    "Aktions-Start",
                    e -> Date.safeDateFormat(e.getAktionspreisGueltigAb(), Date.INSTANT_DATE))
                .withPreferredWidth(80)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)),
            Columns.<CatalogEntry>create(
                    "Aktions-Ende",
                    e -> Date.safeDateFormat(e.getAktionspreisGueltigBis(), Date.INSTANT_DATE))
                .withPreferredWidth(80)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)),
            Columns.<CatalogEntry>create(
                    "E-Pfand",
                    e ->
                        String.format(
                            "%.2f€", getDepositPrice(e.getByArticleNo(e.getPfandNrLadeneinheit()))))
                .withPreferredWidth(80)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<CatalogEntry>create(
                    "G-Pfand",
                    e ->
                        String.format(
                            "%.2f€",
                            getDepositPrice(e.getByArticleNo(e.getPfandNrBestelleinheit()))))
                .withPreferredWidth(80)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<CatalogEntry>create(
                    "Änderungsdatum",
                    e -> Date.safeDateFormat(e.getAenderungsDatum(), Date.INSTANT_DATE))
                .withPreferredWidth(80)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)),
            Columns.create("Barcode", CatalogEntry::getEanLadenEinheit).withPreferredWidth(80),
            Columns.create("Bestelleinheit", CatalogEntry::getBestelleinheit)
                .withPreferredWidth(120),
            Columns.<CatalogEntry>createIconColumn(
                    "AuswW", e -> Icons.booleanIcon(e.getGewichtsartikel()))
                .withPreferredWidth(50),
            Columns.<CatalogEntry>create("MWSt.", e -> e.getMwstKennung().getName())
                .withPreferredWidth(80),
            Columns.<CatalogEntry>create(
                    "Gültig bis",
                    e -> Date.safeDateFormat(e.getKatalogGueltigBis(), Date.INSTANT_DATE))
                .withPreferredWidth(80)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)));

    this.capture =
        new BarcodeCapture(
            e -> {
              objectViewController.setSearch(e);
              refreshList();
            });

    objectViewController.addComponents(catalogFilter.createFilterUIComponents());
    // objectViewController.setForceExtraButtonState(false);
  }

  void refreshList() {
    objectViewController.search();
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return capture.processKeyEvent(e);
  }

  @NotNull
  @Override
  public EditCatalogModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditCatalogView editCatalogView) {
    objectViewController.setSearch("");
    refreshList();
  }

  public ObjectViewView<CatalogEntry> getObjectView() {
    return objectViewController.getView();
  }
}
