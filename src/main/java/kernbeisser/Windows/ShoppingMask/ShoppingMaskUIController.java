package kernbeisser.Windows.ShoppingMask;

import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import org.jetbrains.annotations.NotNull;

public class ShoppingMaskUIController implements Controller<ShoppingMaskUIView,ShoppingMaskModel> {
    private ShoppingMaskUIView view;
    private ShoppingMaskModel model;
    private ShoppingCartController shoppingCartController;

    public ShoppingMaskUIController(SaleSession saleSession) {
        model = new ShoppingMaskModel(saleSession);
        this.shoppingCartController = new ShoppingCartController(model.getValue(), model.getSaleSession()
                                                                                        .getCustomer()
                                                                                        .getSolidaritySurcharge());
        this.view = new ShoppingMaskUIView(this, shoppingCartController);
    }

    void addToShoppingCart() {
        boolean stack = (view.getOption() == ShoppingMaskUIView.ARTICLE_NUMBER);
        try {
            shoppingCartController.addShoppingItem(extractShoppingItemFromUI(), stack);
        } catch (UndefinedInputException undefinedInputException) {
            view.noArticleFound();
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

    double getPrice(Article article) {
        return PriceCalculator.getItemPrice(article, 0, model.getSaleSession().getCustomer().getSolidaritySurcharge());
    }

    private ShoppingItem extractShoppingItemFromUI() throws UndefinedInputException {
        double netPrice = PriceCalculator.getNetFromGross(view.getPriceVATIncluded(),view.getSelectedVAT().getValue());
        double netDeposit = PriceCalculator.getNetFromGross(view.getDeposit(),view.getSelectedVAT().getValue());
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
                    if (extractedArticle == null) throw new UndefinedInputException();
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
                return ShoppingItem.createBakeryProduct(netPrice);
            case ShoppingMaskUIView.DEPOSIT:
                return ShoppingItem.createDeposit(netDeposit);
            case ShoppingMaskUIView.CUSTOM_PRODUCT:
                //TODO wie soll Pfand bei freien Artikeln behandelt werden, evtl gar nicht?
                ShoppingItem customItem = new ShoppingItem();
                customItem.setItemMultiplier((int) view.getAmount());
                customItem.setItemNetPrice(netPrice);
                customItem.setName(view.getItemName());
                customItem.setVat(view.getSelectedVAT().getValue());
                customItem.setMetricUnits(MetricUnits.STACK);
                customItem.setAmount(1);
                return customItem;
            case ShoppingMaskUIView.PRODUCE:
                return ShoppingItem.createOrganic(netPrice);
            case ShoppingMaskUIView.RETURN_DEPOSIT:
                return ShoppingItem.createDeposit(netDeposit * (-1));
            default:
                return null;
        }
    }


    @Override
    public @NotNull ShoppingMaskUIView getView() {
        return view;
    }

    @Override
    public @NotNull ShoppingMaskModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.loadUserInfo(model.getSaleSession());
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    void startPay() {
        new PayController(null, model.getSaleSession(), shoppingCartController.getItems(), () -> {

        });
    }

    void openSearchWindow() {
        new ArticleSelectorController(view::loadItemStats);
    }

//    void editUserAction() {
//        new EditUserController(view, model.getSaleSession().getCustomer(), Mode.EDIT);
//    }
}
