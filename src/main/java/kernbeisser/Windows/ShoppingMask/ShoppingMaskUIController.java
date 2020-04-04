package kernbeisser.Windows.ShoppingMask;

import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Price.PriceCalculator;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
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
        try {
            shoppingCartController.addShoppingItem(extractShoppingItemFromUI());
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
        // TODO HEI Review nettopricing
        int nettoizedPrice = PriceCalculator.getNetFromGross(view.getPrice(),view.isVatLow());    switch (view.getOption()) {
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
                //TODO HEI review price nettoization
                return ShoppingItem.createBakeryProduct(view.getPriceVATIncluded());
            case ShoppingMaskUIView.DEPOSIT:
                return ShoppingItem.createDeposit(view.getDeposit());
            case ShoppingMaskUIView.CUSTOM_PRODUCT:
                ShoppingItem customItem = new ShoppingItem();
                customItem.setItemMultiplier((int) view.getAmount());
                //TODO HEI review price nettoization
                customItem.setItemNetPrice(view.getPriceVATIncluded());
                customItem.setName(view.getItemName());
                customItem.setVat(view.getSelectedVAT().getValue());
                customItem.setMetricUnits(MetricUnits.STACK);
                customItem.setAmount(customItem.getMetricUnits().toUnit(view.getAmount()));
                customItem.setItemMultiplier(view.getArticleAmount());
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
        new ArticleSelectorController(view,view::loadItemStats);
    }
}
