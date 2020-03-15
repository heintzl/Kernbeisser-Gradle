package kernbeisser.Windows.ShoppingMask;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Article;
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
        ShoppingItem item = extractShoppingItemFromUI();
        if (item != null) {
            shoppingCartController.addShoppingItem(item);
        }
    }

    void searchByKbNumber() {
        view.defaultSettings();
        Article found = model.getByKbNumber(view.getKBArticleNumber());
        if (found != null) {
            view.loadItemStats(found);
        }
    }

    void searchBySupplierItemsNumber() {
        view.defaultSettings();
        Article found = model.getBySupplierItemNumber(view.getSuppliersNumber());
        if (found != null) {
            view.loadItemStats(found);
        }
    }

    int getPrice(Article article) {
        return PriceCalculator.getItemPrice(article, 0, model.getSaleSession().getCustomer().getSolidaritySurcharge());
    }

    private ShoppingItem extractShoppingItemFromUI() {
        switch (view.getOption()) {
            case ShoppingMaskUIView.ARTICLE_NUMBER:
                Article extractedArticle = null;
                int kbArticleNumber = view.getKBArticleNumber();
                if (kbArticleNumber != 0) {
                    extractedArticle = model.getByKbNumber(kbArticleNumber);
                }
                if (extractedArticle == null) {
                    int supplier = view.getSuppliersNumber();
                    if (supplier != 0) {
                        extractedArticle = model.getBySupplierItemNumber(supplier);
                    }
                    if (extractedArticle == null) {
                        view.noArticleFound();
                        return null;
                        //TODO throw NoResultException instead of null
                    }
                }
                ShoppingItem shoppingItem = new ShoppingItem(extractedArticle);
                shoppingItem.setDiscount(view.getDiscount());
                view.setDiscount();
                if (shoppingItem.isWeighable()) {
                    shoppingItem.setAmount(extractedArticle.getMetricUnits().toUnit(view.getAmount()));
                    shoppingItem.setItemMultiplier(1);
                } else {
                    shoppingItem.setItemMultiplier((int) view.getAmount());
                }
                return shoppingItem;
            case ShoppingMaskUIView.BAKED_GOODS:
                return ShoppingItem.createBakeryProduct(view.getPriceVATIncluded());
            case ShoppingMaskUIView.DEPOSIT:
                return ShoppingItem.createDeposit(view.getDeposit());
            case ShoppingMaskUIView.CUSTOM_PRODUCT:
                ShoppingItem customItem = new ShoppingItem();
                customItem.setItemMultiplier((int) view.getAmount());
                customItem.setItemNetPrice(view.getPriceVATIncluded());
                customItem.setName(view.getItemName());
                customItem.setAmount(customItem.getMetricUnits().toUnit(view.getAmount()));
                return customItem;
            case ShoppingMaskUIView.PRODUCE:
                return ShoppingItem.createOrganic(view.getPriceVATIncluded());
            case ShoppingMaskUIView.RETURN_DEPOSIT:
                return ShoppingItem.createDeposit(view.getDeposit()*(-1));
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
        new DefaultSearchPanelController<Article>(Article::defaultSearch, view::loadItemStats,
                                                  Column.create("Name", Article::getName),
                                                  Column.create("Barcode", Article::getBarcode),
                                                  Column.create("Lieferant",e -> e.getSupplier().getShortName())
        ).getView().asWindow(view);
    }
}
