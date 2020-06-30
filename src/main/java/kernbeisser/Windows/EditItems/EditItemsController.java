package kernbeisser.Windows.EditItems;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditItem.EditItemController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.ObjectView.ObjectViewView;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

public class EditItemsController implements Controller<EditItemsView,EditItemsModel> {
    private final EditItemsView view;
    private final EditItemsModel model;
    private final ObjectViewController<Article> objectViewController;

    public EditItemsController() {
        this.model = new EditItemsModel();
        objectViewController = new ObjectViewController<Article>(
                EditItemController::new, Article::defaultSearch,
                Column.create("Name", Article::getName, PermissionKey.ARTICLE_NAME_READ),
                Column.create("Packungsgröße",e -> (e.getAmount())+e.getMetricUnits().getShortName()),
                Column.create("Ladennummer", Article::getKbNumber, PermissionKey.ARTICLE_KB_NUMBER_READ),
                Column.create("Lieferantenummer", Article::getSuppliersItemNumber, PermissionKey.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ),
                Column.create("Auswiegware", e -> e.isWeighAble() ?  "Ja" : "Nein", PermissionKey.ARTICLE_WEIGHABLE_READ),
                Column.create("Nettopreis",e -> String.format("%.2f€",e.getNetPrice()), PermissionKey.ARTICLE_NET_PRICE_READ),
                Column.create("Einzelpfand",e -> String.format("%.2f€",e.getSingleDeposit()), PermissionKey.ARTICLE_SINGLE_DEPOSIT_READ),
                Column.create("MwSt.",e -> e.getVat().getName(), PermissionKey.ARTICLE_VAT_READ),
                Column.create("Gebindegrösse.", Article::getContainerSize),
                Column.create("Preisliste", Article::getPriceList, PermissionKey.ARTICLE_PRICE_LIST_READ),
                Column.create("Barcode", Article::getBarcode, PermissionKey.ARTICLE_BARCODE_READ)
        );
        objectViewController.initView();
        objectViewController.setSearch("");
        this.view = new EditItemsView(this);

    }


    @NotNull
    @Override
    public EditItemsView getView() {
        return view;
    }

    @NotNull
    @Override
    public EditItemsModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public ObjectViewView<Article> getObjectView() {
        return objectViewController.getView();
    }


    private Window w = null;
    void openPriceListSelection(){
        PriceListTree pt = new PriceListTree();
        pt.addSelectionListener(e -> {
            objectViewController.setSearch(e.toString());
            objectViewController.search();
            w.back();
        });
        w = Controller.createFakeController(pt).openAsWindow(getView().getWindow(),SubWindow::new);
    }
}
