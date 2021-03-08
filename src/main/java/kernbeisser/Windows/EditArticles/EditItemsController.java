package kernbeisser.Windows.EditArticles;

import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;

import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.StripedRenderer;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormImplemetations.Article.ArticleController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Security.Requires;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

@Requires(PermissionKey.ACTION_OPEN_EDIT_ARTICLES)
public class EditItemsController extends Controller<EditItemsView, EditItemsModel> {

  private final ObjectViewController<Article> objectViewController;

  private final BarcodeCapture capture;

  public EditItemsController() {
    super(new EditItemsModel());
    objectViewController =
        new ObjectViewController<>(
            "Artikel bearbeiten",
            new ArticleController(),
            this::search,
            true,
            new Column<Article>() {
              @Override
              public String getName() {
                return "Name";
              }

              @Override
              public Object getValue(Article article) throws PermissionKeyRequiredException {
                return article.getName();
              }

              @Override
              public void adjust(TableColumn column) {
                column.setMinWidth(600);
              }

              @Override
              public TableCellRenderer getRenderer() {
                StripedRenderer renderer = new StripedRenderer();
                renderer.setAlignmentX(LEFT);
                return renderer;
              }
            },
            Column.create(
                "Packungsgröße", e -> e.getAmount() + e.getMetricUnits().getShortName(), RIGHT),
            Column.create("Ladennummer", Article::getKbNumber, RIGHT),
            Column.create("Lieferant", Article::getSupplier, LEFT),
            Column.create("Lieferantenummer", Article::getSuppliersItemNumber, RIGHT),
            Column.create("Auswiegware", e -> e.isWeighable() ? "Ja" : "Nein", LEFT),
            Column.create("Nettopreis", e -> String.format("%.2f€", e.getNetPrice()), RIGHT),
            Column.create("Einzelpfand", e -> String.format("%.2f€", e.getSingleDeposit()), RIGHT),
            Column.create("MwSt.", e -> e.getVat().getName(), RIGHT),
            Column.create("Gebindegröße", Article::getContainerSize, RIGHT),
            Column.create("Preisliste", Article::getPriceList, LEFT),
            Column.create("Barcode", Article::getBarcode, RIGHT));
    this.capture =
        new BarcodeCapture(
            e -> objectViewController.openForm(Article.getByBarcode(Long.parseLong(e)), Mode.EDIT));
  }

  private Collection<Article> search(String query, int max) {
    return Article.getDefaultAll(
        query, e -> (!(getView().showOnlyShopRange() && !e.isShopRange())), max);
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
  public EditItemsModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditItemsView editItemsView) {
    objectViewController.setSearch("");
    refreshList();
  }

  public ObjectViewView<Article> getObjectView() {
    return objectViewController.getView();
  }

  void openPriceListSelection() {
    var view = getView();
    ObjectTree<PriceList> priceListObjectTree = new ObjectTree<>(PriceList.getPriceListsAsNode());
    priceListObjectTree.addSelectionListener(
        e -> {
          objectViewController.setSearch(e.toString());
          objectViewController.search();
          IView.traceViewContainer(priceListObjectTree.getParent());
        });
    new ComponentController(priceListObjectTree).openIn(new SubWindow(view.traceViewContainer()));
  }
}
