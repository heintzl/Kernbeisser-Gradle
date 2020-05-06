package kernbeisser.Windows.EditItems;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.PriceListTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.Key;
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
                Column.create("Name", Article::getName, Key.ARTICLE_NAME_READ),
                Column.create("Packungsgröße",e -> (e.getAmount())+e.getMetricUnits().getShortName()),
                Column.create("Ladennummer", Article::getKbNumber,Key.ARTICLE_KB_NUMBER_READ),
                Column.create("Lieferantenummer", Article::getSuppliersItemNumber,Key.ARTICLE_SUPPLIERS_ITEM_NUMBER_READ),
                Column.create("Auswiegware", e -> e.isWeighAble() ?  "Ja" : "Nein",Key.ARTICLE_WEIGHABLE_READ),
                Column.create("Nettopreis",e -> String.format("%.2f€",e.getNetPrice()),Key.ARTICLE_NET_PRICE_READ),
                Column.create("Einzelpfand",e -> String.format("%.2f€",e.getSingleDeposit()),Key.ARTICLE_SINGLE_DEPOSIT_READ),
                Column.create("MwSt.",e -> e.getVAT().getName(),Key.ARTICLE_VAT_READ),
                Column.create("Gebindegrösse.", Article::getContainerSize),
                Column.create("Preisliste", Article::getPriceList,Key.ARTICLE_PRICE_LIST_READ),
                Column.create("Barcode",Article::getBarcode,Key.ARTICLE_BARCODE_READ)
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
    public Key[] getRequiredKeys() {
        return new Key[0];
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
