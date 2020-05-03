package kernbeisser.Windows.ShoppingMask;

import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;


public class ShoppingMaskUIController implements Controller<ShoppingMaskUIView,ShoppingMaskModel> {
    private final ShoppingMaskUIView view;
    private final ShoppingMaskModel model;
    private final ShoppingCartController shoppingCartController;

    public ShoppingMaskUIController(SaleSession saleSession) {
        model = new ShoppingMaskModel(saleSession);
        this.shoppingCartController = new ShoppingCartController(model.getValue(), model.getSaleSession()
                                                                                        .getCustomer()
                                                                                        .getSolidaritySurcharge());
        shoppingCartController.initView();
        this.view = new ShoppingMaskUIView(this, shoppingCartController);
    }

    void addToShoppingCart() {
        boolean piece = (view.getOption() == ShoppingMaskUIView.ARTICLE_NUMBER || view.getOption() == ShoppingMaskUIView.CUSTOM_PRODUCT);
        try {
            shoppingCartController.addShoppingItem(extractShoppingItemFromUI(), piece);
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
        ShoppingItem si = new ShoppingItem(article, 0,false);
        return si.getItemRetailPrice();//PriceCalculator.getItemPrice(article, 0, model.getSaleSession().getCustomer().getSolidaritySurcharge());
    }

    private ShoppingItem extractShoppingItemFromUI() throws UndefinedInputException {
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
                        throw new UndefinedInputException();
                    }
                }
                ShoppingItem shoppingItem = new ShoppingItem(extractedArticle, view.getDiscount(),false);
                view.setDiscount();
                shoppingItem.setItemMultiplier((int) view.getAmount());
                return shoppingItem;
            case ShoppingMaskUIView.BAKED_GOODS:
                return ShoppingItem.createBakeryProduct(view.getPriceVATIncluded());
            case ShoppingMaskUIView.PRODUCE:
                return ShoppingItem.createOrganic(view.getPriceVATIncluded());
            case ShoppingMaskUIView.CUSTOM_PRODUCT:
                Article customArticle = new Article();
                customArticle.setName( // add price to name so that same name but different price results in different hash
                                       view.getItemName());
                                       //MessageFormat.format("{0} à {1, number,#0.00}\u20AC", view.getItemName(), view.getPriceVATIncluded(), view.getSelectedVAT().getValue() *100));
                customArticle.setVAT(view.getSelectedVAT());
                customArticle.setNetPrice(view.getPriceVATIncluded() / (1. + view.getSelectedVAT().getValue()));
                customArticle.setMetricUnits(MetricUnits.PIECE);
                ShoppingItem customItem = new ShoppingItem(customArticle,0,false);
                customItem.setItemMultiplier((int) view.getAmount());
                return customItem;
            case ShoppingMaskUIView.DEPOSIT:
                return ShoppingItem.createDeposit(view.getDeposit());
            case ShoppingMaskUIView.RETURN_DEPOSIT:
                return ShoppingItem.createDeposit(view.getDeposit() * (-1));
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
            getView().back();
        }).openAsWindow(view.getWindow(), SubWindow::new);
    }

    void openSearchWindow() {
        new ArticleSelectorController(view::loadItemStats);
    }

    void editUserAction() {
        new EditUserController(model.getSaleSession().getCustomer(), Mode.EDIT);
    }
}
