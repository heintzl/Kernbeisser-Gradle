package kernbeisser.Windows.EditItems;

import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.EditItem.EditItemController;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.ObjectView.ObjectViewView;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

public class EditItemsController implements IController<EditItemsView, EditItemsModel> {

  private EditItemsView view;
  private final EditItemsModel model;
  private final ObjectViewController<Article> objectViewController;

  @Linked private final BarcodeCapture capture;

  public EditItemsController() {
    this.model = new EditItemsModel();
    objectViewController =
        new ObjectViewController<>(
            EditItemController::new,
            Article::defaultSearch,
            true,
            Column.create("Name", Article::getName),
            Column.create(
                "Packungsgröße", e -> (e.getAmount()) + e.getMetricUnits().getShortName()),
            Column.create("Ladennummer", Article::getKbNumber),
            Column.create("Lieferant", Article::getSupplier),
            Column.create("Lieferantenummer", Article::getSuppliersItemNumber),
            Column.create("Auswiegware", e -> e.isWeighable() ? "Ja" : "Nein"),
            Column.create("Nettopreis", e -> String.format("%.2f€", e.getNetPrice())),
            Column.create("Einzelpfand", e -> String.format("%.2f€", e.getSingleDeposit())),
            Column.create("MwSt.", e -> e.getVat().getName()),
            Column.create("Gebindegrösse.", Article::getContainerSize),
            Column.create("Preisliste", Article::getPriceList),
            Column.create("Barcode", Article::getBarcode));

    this.capture =
        new BarcodeCapture(
            e -> {
              objectViewController.setSearch(e);
              objectViewController.search();
            });
  }

  @NotNull
  @Override
  public EditItemsModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    objectViewController.setSearch("");
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public ObjectViewView<Article> getObjectView() {
    return objectViewController.getView();
  }

  private Window w = null;

  void openPriceListSelection() {
    ObjectTree<PriceList> priceListObjectTree = new ObjectTree<>(PriceList.getPriceListsAsNode());
    priceListObjectTree.addSelectionListener(
        e -> {
          objectViewController.setSearch(e.toString());
          objectViewController.search();
          w.back();
        });
    w =
        IController.createFakeController(priceListObjectTree)
            .openAsWindow(getView().getWindow(), SubWindow::new);
  }
}
