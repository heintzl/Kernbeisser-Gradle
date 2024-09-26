package kernbeisser.Windows.EditArticles;

import static javax.swing.SwingConstants.*;

import jakarta.persistence.NoResultException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.Columns.CustomizableColumn;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.CustomComponents.SearchBox.Filters.ArticleFilter;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Enums.ArticleDeletionResult;
import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormImplemetations.Article.ArticleController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Icons;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.PrintLabels.PrintLabelsController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class EditArticlesController extends Controller<EditArticlesView, EditArticlesModel> {

  private final ObjectViewController<Article> objectViewController;

  private final BarcodeCapture capture;

  private final ArticleFilter articleFilter = new ArticleFilter(this::refreshList);

  private boolean hasAdminTools = false;
  private JButton mergeCatalog;
  private JToggleButton editActions;

  private String formatArticleSurcharge(Article article) {
    return article.getSurchargeGroup().getName()
        + " ("
        + Math.round(article.getSurchargeGroup().getSurcharge() * 100)
        + "%)";
  }

  private Map<Article, String> differences = new HashMap<>();

  @Key(PermissionKey.ACTION_OPEN_EDIT_ARTICLES)
  public EditArticlesController() {
    super(new EditArticlesModel());
    Map<Integer, Instant> lastDeliveries = ArticleRepository.getLastDeliveries();
    objectViewController =
        new ObjectViewController<>(
            "Artikel bearbeiten",
            new ArticleController(),
            articleFilter::searchable,
            true,
            new CustomizableColumn<>("Name", Article::getName)
                .withDefaultFilter()
                .withColumnAdjustor(column -> column.setPreferredWidth(350))
                .withHorizontalAlignment(LEFT),
            new CustomizableColumn<Article>(
                    "Packungsgröße", e -> e.getAmount() + e.getMetricUnits().getShortName())
                .withHorizontalAlignment(RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Ladennummer", Article::getKbNumber, RIGHT)
                .withSorter(Column.NUMBER_SORTER)
                .withDefaultFilter(),
            Columns.<Article>createIconColumn("Aktion", a -> Icons.booleanIcon(a.isOffer()))
                .withHorizontalAlignment(CENTER)
                .withLeftClickConsumer(this::toggleAction),
            Columns.create("Lieferant", Article::getSupplier, LEFT)
                .withDefaultFilter()
                .withColumnAdjustor(e -> e.setPreferredWidth(150)),
            Columns.create("Hersteller", Article::getProducer, LEFT)
                .withDefaultFilter()
                .withColumnAdjustor(e -> e.setPreferredWidth(100)),
            Columns.create("Lieferantennummer", Article::getSuppliersItemNumber, RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            new CustomizableColumn<Article>("Auswiegware", e -> e.isWeighable() ? "Ja" : "Nein")
                .withDefaultFilter(),
            new CustomizableColumn<Article>(
                    "Nettopreis", e -> String.format("%.2f€", e.getNetPrice()))
                .withHorizontalAlignment(RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            new CustomizableColumn<Article>(
                    "Verkaufspreis",
                    e ->
                        String.format(
                            "%.2f€", ArticleRepository.calculateArticleRetailPrice(e, 0, false)))
                .withHorizontalAlignment(RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Einzelpfand", e -> String.format("%.2f€", e.getSingleDeposit()), RIGHT),
            new CustomizableColumn<Article>("MwSt.", e -> e.getVat().getName())
                .withHorizontalAlignment(RIGHT)
                .withDefaultFilter(),
            new CustomizableColumn<Article>(
                    "Gebindegröße", e -> String.format("%.3f", e.getContainerSize()))
                .withHorizontalAlignment(RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Preisliste", Article::getPriceList, LEFT)
                .withColumnAdjustor(e -> e.setPreferredWidth(200))
                .withDefaultFilter(),
            Columns.<Article>create(
                    "Zuschlaggruppe", e -> e.getSurchargeGroup().getNameWithSurcharge(), LEFT)
                .withDefaultFilter(),
            Columns.create("Barcode", Article::getBarcode, RIGHT),
            Columns.<Article>create(
                    "Letzte Lief.",
                    a ->
                        Date.safeDateFormat(lastDeliveries.get(a.getKbNumber()), Date.INSTANT_DATE))
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)));

    this.capture =
        new BarcodeCapture(
            e ->
                objectViewController.openForm(
                    ArticleRepository.getByBarcode(Long.parseLong(e))
                        .orElseThrow(NoResultException::new),
                    Mode.EDIT));
    objectViewController.addComponents(articleFilter.createFilterUIComponents());
    objectViewController.setForceExtraButtonState(false);
  }

  String displayDifference(Article article) {
    return Tools.ifNull(differences.get(article), "");
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
  public EditArticlesModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditArticlesView editArticlesView) {
    objectViewController.setSearch("");
    if (Tools.canInvoke(PrintLabelsController::new)) {
      objectViewController.addButton(
          PrintLabelsController.getLaunchButton(getView().traceViewContainer()));
    }
    refreshList();
    Tools.canInvoke(this::addAdministrationTools);
    Tools.canInvoke(this::addEditActionsButton);
  }

  public ObjectViewView<Article> getObjectView() {
    return objectViewController.getView();
  }

  void openPriceListSelection() {
    EditArticlesView view = getView();
    ObjectTree<PriceList> priceListObjectTree = new ObjectTree<>(PriceList.getPriceListsAsNode());
    priceListObjectTree.addSelectionListener(
        e -> {
          objectViewController.setSearch("PL:" + e.getValue().getName());
          objectViewController.search();
          IView.traceViewContainer(priceListObjectTree.getParent()).requestClose();
        });
    new ComponentController(priceListObjectTree, "Preisliste auswählen:")
        .openIn(new SubWindow(view.traceViewContainer()));
  }

  private void toggleAction(Article article) {
    if (!editActions.isSelected()) {
      return;
    }
    article.setOffer(!article.isOffer());
    Tools.merge(article);
    objectViewController.getSearchBoxView().getObjectTable().repaint();
  }

  private void toggleEditActionsButton() {
    if (editActions.isSelected()) {
      editActions.setForeground(Color.GREEN);
      editActions.setIcon(Icons.actionActiveIcon);
    } else {
      editActions.setForeground(Color.BLACK);
      editActions.setIcon(Icons.actionInactiveIcon);
    }
    ;
  }

  @Key({PermissionKey.ARTICLE_OFFER_WRITE, PermissionKey.ACTION_OPEN_SPECIAL_PRICE_EDITOR})
  private void addEditActionsButton() {
    editActions = new JToggleButton("Aktionen bearbeiten");
    editActions.setIcon(Icons.actionInactiveIcon);
    editActions.setToolTipText("Macht die Aktions-Spalte bearbeitbar");
    editActions.addActionListener(e -> toggleEditActionsButton());
    objectViewController.addButton(editActions);
  }

  @Key(PermissionKey.ACTION_OPEN_ADMIN_TOOLS)
  private void addAdministrationTools() {
    if (hasAdminTools) return;
    JButton previewCatalog = new JButton("Katalog-Abweichungen anzeigen");
    previewCatalog.setIcon(IconFontSwing.buildIcon(FontAwesome.BARCODE, 20, Color.DARK_GRAY));
    previewCatalog.setToolTipText("Zeigt Unterschiede zwischen Katalog und Artikel an");
    objectViewController.addButton(previewCatalog, e -> previewCatalog());
    mergeCatalog = new JButton("Übertrage Pfand und Barcode aus Katalog");
    mergeCatalog.setIcon(IconFontSwing.buildIcon(FontAwesome.BARCODE, 20, Color.DARK_GRAY));
    mergeCatalog.setToolTipText(
        "Übernimmt für ausgewählte Artikel Pfand und Barcode aus dem Katalog.");
    mergeCatalog.setEnabled(false);
    objectViewController.addButton(mergeCatalog, e -> mergeCatalog());
    JButton removeSelected = new JButton("Markierte Artikel entfernen");
    removeSelected.setIcon(
        IconFontSwing.buildIcon(FontAwesome.TRASH_O, 20, new Color(100, 30, 30)));
    removeSelected.setToolTipText("Entfernt alle martkierten Artikel, soweit möglich");
    removeSelected.addActionListener(e -> removeSelected());
    objectViewController.addButton(removeSelected);
    hasAdminTools = true;
  }

  private void mergeCatalog() {
    EditArticlesView view = getView();
    if (differences.isEmpty()) {
      view.messageNoDifferences();
      return;
    }
    Collection<Article> articlesToMerge =
        objectViewController.getSearchBoxController().getSelectedObjects();
    if (articlesToMerge.isEmpty()) {
      view.messageNoSelection();
      return;
    }
    List<String> mergeLog = model.mergeCatalog(articlesToMerge);
    previewCatalog();
    view.showLog(mergeLog);
  }

  private void previewCatalog() {
    model.previewCatalog(objectViewController.getSearchBoxController().getFilteredObjects());
    if (model.getDifferences().isEmpty()) {
      getView().messageNoDifferences();
    } else {
      CustomizableColumn<Article> catalogDifference =
          new CustomizableColumn<Article>("Katalog-Abweichungen", this::displayDifference)
              .withDefaultFilter()
              .withPreferredWidth(200);
      this.differences =
          model.getDifferences().stream()
              .collect(
                  Collectors.toMap(
                      ArticleComparedToCatalogEntry::getArticle,
                      ArticleComparedToCatalogEntry::getDescription));
      ObjectTable<Article> table = objectViewController.getSearchBoxView().getObjectTable();
      if (table.getColumns().stream()
          .noneMatch(c -> c.getName().equals(catalogDifference.getName()))) {
        table.addColumn(catalogDifference);
      }
      objectViewController.search();
      mergeCatalog.setEnabled(true);
    }
  }

  private void removeSelected() {
    EditArticlesView view = getView();
    Map<ArticleDeletionResult, List<Article>> preparedArticles =
        model.prepareRemoval(objectViewController.getSearchBoxController().getSelectedObjects());
    if (EditArticlesView.confirmDelete(view.getContent(), preparedArticles)) {
      model.remove(preparedArticles);
      objectViewController.search();
    }
    ;
  }
}
