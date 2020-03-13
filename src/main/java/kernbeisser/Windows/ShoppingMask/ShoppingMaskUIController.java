package kernbeisser.Windows.ShoppingMask;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Item;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.DefaultSearchPanel.DefaultSearchPanelController;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.Window;

public class ShoppingMaskUIController implements Controller {
    private ShoppingMaskUIView view;
    private ShoppingMaskModel model;
    private ShoppingCartController shoppingCartController;

    public ShoppingMaskUIController(Window current, SaleSession saleSession) {
        model = new ShoppingMaskModel(saleSession);
        this.shoppingCartController = new ShoppingCartController(model.getValue(), model.getSaleSession()
                                                                                        .getCustomer()
                                                                                        .getSolidaritySurcharge());
        this.view = new ShoppingMaskUIView(current, this, shoppingCartController);
        view.loadUserInfo(saleSession);
    }

    void addToShoppingCart() {
        ShoppingItem item = extract();
        if (item != null) {
            shoppingCartController.addShoppingItem(item);
        }
    }

    void searchByKbNumber() {
        view.defaultSettings();
        Item found = model.getByKbNumber(view.getArticleNumber());
        if (found != null) {
            view.loadItemStats(found);
        }
    }

    void searchBySupplierItemsNumber() {
        view.defaultSettings();
        Item found = model.getBySupplierItemNumber(view.getSuppliersNumber());
        if (found != null) {
            view.loadItemStats(found);
        }
    }

    int getPrice(Item item) {
        return PriceCalculator.getItemPrice(item, 0, model.getSaleSession().getCustomer().getSolidaritySurcharge());
    }

    private ShoppingItem extract() {
        switch (view.getOption()) {
            case ShoppingMaskUIView.ARTICLE_NUMBER:
                Item i = null;
                int kb = view.getArticleNumber();
                if (kb != 0) {
                    i = model.getByKbNumber(kb);
                }
                if (i == null) {
                    int supplier = view.getSuppliersNumber();
                    if (supplier != 0) {
                        i = model.getBySupplierItemNumber(supplier);
                    }
                    if (i == null) {
                        view.noArticleFound();
                        return null;
                    }
                }
                ShoppingItem out = new ShoppingItem(i);
                out.setDiscount(view.getDiscount());
                view.setDiscount();
                if (out.isWeighAble()) {
                    out.setAmount(i.getUnit().toUnit(view.getAmount()));
                    out.setItemAmount(1);
                } else {
                    out.setItemAmount((int) view.getAmount());
                }
                return out;
            case ShoppingMaskUIView.BAKED_GOODS:
                return ShoppingItem.getBakeryProduct(view.getPrice());
            case ShoppingMaskUIView.DEPOSIT:
                return ShoppingItem.getDeposit(view.getDeposit());
            case ShoppingMaskUIView.CUSTOM_PRODUCT:
                ShoppingItem o = new ShoppingItem();
                o.setItemAmount((int) view.getAmount());
                o.setItemNetPrice(view.getPrice());
                o.setName(view.getItemName());
                o.setAmount(o.getUnit().toUnit(view.getAmount()));
                return o;
            case ShoppingMaskUIView.PRODUCE:
                return ShoppingItem.getOrganic(view.getPrice());
            case ShoppingMaskUIView.RETURN_DEPOSIT:
                return ShoppingItem.getDeposit(-view.getDeposit());
            default:
                return null;
        }
    }


    @Override
    public ShoppingMaskUIView getView() {
        return view;
    }

    @Override
    public ShoppingMaskModel getModel() {
        return model;
    }

    void startPay() {
        new PayController(view, model.getSaleSession(), model.getShoppingCart(), () -> {
        });
    }

    void openSearchWindow() {
        new DefaultSearchPanelController<Item>(Item::defaultSearch, view::loadItemStats,
                                               Column.create("Name",Item::getName),
                                               Column.create("Barcode",Item::getBarcode),
                                               Column.create("Lieferant",e -> e.getSupplier().getShortName())
        ).getView().asWindow(view);
    }
}
