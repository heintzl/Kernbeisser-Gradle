package kernbeisser.Windows.EditCatalog;

import static kernbeisser.DBEntities.CatalogEntry.CATALOG_ENTRY_STATES;

import java.awt.event.KeyEvent;
import java.util.NoSuchElementException;
import java.util.Optional;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.SearchBox.Filters.CatalogFilter;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.Article.ArticleController;
import kernbeisser.Forms.FormImplemetations.CatalogEntry.CatalogEntryController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Icons;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class EditCatalogController extends Controller<EditCatalogView, EditCatalogModel> {

  private final ObjectViewController<CatalogEntry> objectViewController;

  private final BarcodeCapture capture;

  private final CatalogFilter catalogFilter = new CatalogFilter(this::refreshList, getModel());

  @Key(PermissionKey.ACTION_LOGIN) // TODO add more restrictions, if required
  public EditCatalogController() {
    super(new EditCatalogModel());

    objectViewController =
        new ObjectViewController<>(
            "Katalog",
            new CatalogEntryController(),
            (String s, int max) -> model.searchable(s, max, catalogFilter::matches),
            false,
            Columns.<CatalogEntry>create(
                    "Status", e -> CATALOG_ENTRY_STATES.get(e.getAenderungskennung()))
                .withDefaultFilter()
                .withPreferredWidth(80),
            Columns.create("Artikelnummer", CatalogEntry::getArtikelNr)
                .withDefaultFilter()
                .withPreferredWidth(80)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Bezeichnung", CatalogEntry::getBezeichnung)
                .withDefaultFilter()
                .withPreferredWidth(250),
            Columns.create("Gebinde", CatalogEntry::getBestelleinheit).withPreferredWidth(80),
            Columns.create("Hersteller", CatalogEntry::getMarke)
                .withDefaultFilter()
                .withPreferredWidth(120),
            Columns.create("Herkunft", CatalogEntry::getHerkunft)
                .withDefaultFilter()
                .withPreferredWidth(80),
            Columns.<CatalogEntry>create("Netto-Pr.", e -> String.format("%.2f€", e.getPreis()))
                .withPreferredWidth(60)
                .withSorter(Column.NUMBER_SORTER),
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
            Columns.<CatalogEntry>create("E-Pfand", e -> String.format("%.2f€", e.getEinzelPfand()))
                .withPreferredWidth(80)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<CatalogEntry>create(
                    "G-Pfand", e -> String.format("%.2f€", e.getGebindePfand()))
                .withPreferredWidth(80)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<CatalogEntry>create(
                    "Änderungsdatum",
                    e -> Date.safeDateFormat(e.getAenderungsDatum(), Date.INSTANT_DATE))
                .withPreferredWidth(80)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)),
            Columns.create("Barcode", CatalogEntry::getEanLadenEinheit).withPreferredWidth(80),
            Columns.create("Einheit", CatalogEntry::getLadeneinheit).withPreferredWidth(120),
            Columns.<CatalogEntry>createIconColumn(
                    "AuswW", e -> Icons.booleanIcon(e.getGewichtsartikel()))
                .withPreferredWidth(50),
            Columns.<CatalogEntry>create("MWSt.", e -> e.getMwstKennung().getName())
                .withPreferredWidth(80),
            Columns.<CatalogEntry>create(
                    "Gültig bis",
                    e -> Date.safeDateFormat(e.getKatalogGueltigBis(), Date.INSTANT_DATE))
                .withPreferredWidth(80)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)),
            Columns.<CatalogEntry>createIconColumn(
                    "KB-Stamm", e -> Icons.catalogArticleIcon(getModel().isArticleOffer(e)))
                .withLeftClickConsumer(this::makeArticle));

    this.capture =
        new BarcodeCapture(
            e -> {
              objectViewController.setSearch(e);
              refreshList();
            });

    objectViewController.addComponents(catalogFilter.createFilterUIComponents());
    objectViewController.setForceExtraButtonState(true);
  }

  @Key(PermissionKey.ACTION_OPEN_SPECIAL_PRICE_EDITOR)
  private void makeArticle(CatalogEntry entry) {
    try {
      getModel()
          .isArticleOffer(entry)
          .ifPresentOrElse(
              offer -> {
                Article article = ArticleRepository.getArticleFromCatalogEntry(entry).orElseThrow();
                if (getView().confirmOfferChange(article, offer)) {
                  model.setOffer(article, !offer);
                }
              },
              () -> {
                Optional<ArticleOptions> confirmation =
                    getView().confirmNewArticle(entry, entry.isOffer());
                confirmation.ifPresent(
                    options -> {
                      Article article = model.makeArticle(entry, options.offer());
                      if (options.openArticle()) {
                        FormEditorController.create(article, new ArticleController(), Mode.EDIT)
                            .openIn(new SubWindow(getView().traceViewContainer()));
                      }
                    });
              });
    } catch (NoSuchElementException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
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
