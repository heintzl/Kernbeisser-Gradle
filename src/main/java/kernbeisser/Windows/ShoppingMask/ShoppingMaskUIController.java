package kernbeisser.Windows.ShoppingMask;

import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Mode;
import kernbeisser.Exeptions.UndefinedInputException;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditUser.EditUserController;
import kernbeisser.Windows.Pay.PayController;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


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

    boolean addToShoppingCart() {
        boolean piece = (view.getOption() == ShoppingMaskUIView.ARTICLE_NUMBER || view.getOption() == ShoppingMaskUIView.CUSTOM_PRODUCT);
        boolean success = false;
        try {
            ShoppingItem item = extractShoppingItemFromUI();
            if (item.getItemMultiplier() != 0) {
                shoppingCartController.addShoppingItem(extractShoppingItemFromUI(), piece);
                success = true;
            }
        } catch (UndefinedInputException undefinedInputException) {
            view.noArticleFound();
            return false;
        } finally {
            return success;
        }
    }

    void searchByKbNumber() {
        view.defaultSettings();
        Article found = model.getByKbNumber(view.getKBArticleNumber());
        if (found != null) {
            view.loadItemStats(found);
        } else  {view.setSuppliersItemNumber("");}
    }

    void searchBySupplierItemsNumber() {
        view.defaultSettings();
        Article found = model.getBySupplierItemNumber(view.getSuppliersNumber());
        if (found != null) {
            view.loadItemStats(found);
        } else { view.setKbNumber("");}
    }

    void searchByBarcode(long barcode) {
        view.setOptArticleNo();
        Article found = model.getByBarcode(barcode);
        if (found != null) {
            view.loadItemStats(found);
            view.addToCart();
        } else {
            JOptionPane.showMessageDialog( view.getContent(), "Konnte keinen Artikel mit Barcode \"" + barcode + "\" finden", "Artikel nicht gefunden", JOptionPane.INFORMATION_MESSAGE);
            view.setKbNumber("");}
    }

    double getPrice(Article article) {
        ShoppingItem shoppingItem = new ShoppingItem(article, 0,false);
        return shoppingItem.getItemRetailPrice();
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
                customArticle.setName(view.getItemName());
                customArticle.setVat(view.getSelectedVAT());
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
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    void startPay() {
        new PayController(null, model.getSaleSession(), shoppingCartController.getItems(), () -> {
            getView().back();
        }).openAsWindow(view.getWindow(), SubWindow::new);
    }

    void openSearchWindow() {
        new ArticleSelectorController(view::loadItemStats).openAsWindow(view.getWindow(),SubWindow::new);
    }

    void editUserAction() {
        new EditUserController(model.getSaleSession().getCustomer(), Mode.EDIT).openAsWindow(view.getWindow(),SubWindow::new);
    }

    public void processBarcode(String barcode) {
        try {
            long bc = Long.parseLong(barcode);
            searchByBarcode(bc);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view.getContent(),
                                          "Ungültiger Barcode: " + barcode,
                                          "Barcode Fehler",
                                          JOptionPane.WARNING_MESSAGE);
        }

    }
}
