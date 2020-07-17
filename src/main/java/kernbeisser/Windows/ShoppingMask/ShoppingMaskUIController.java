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
import java.awt.*;
import java.text.MessageFormat;


public class ShoppingMaskUIController implements Controller<ShoppingMaskUIView,ShoppingMaskModel> {
    private final ShoppingMaskUIView view;
    private final ShoppingMaskModel model;
    private final ShoppingCartController shoppingCartController;

    public ShoppingMaskUIController(SaleSession saleSession) {
        model = new ShoppingMaskModel(saleSession);
        this.shoppingCartController = new ShoppingCartController(model.getValue(), model.getSaleSession()
                                                                                        .getCustomer()
                                                                                        .getSolidaritySurcharge(),true);
        shoppingCartController.initView();
        this.view = new ShoppingMaskUIView(this, shoppingCartController);
    }

    private boolean checkStorno(ShoppingItem item, boolean piece) {
        boolean result = true;
        boolean exit = true;
        String response = "";
        if (piece && item.getItemMultiplier() < 0) {
            response = view.inputStornoRetailPrice(item.getItemRetailPrice(), false);
            do {
                if (response == null || response.hashCode() == 0 || response.hashCode() == 48) {
                    exit = true;
                    result = false;
                } else {
                    try {
                        double alteredRetailPrice = Double.parseDouble(response.replace(',', '.'));
                        if (alteredRetailPrice > 0) {
                            if (alteredRetailPrice != item.getItemRetailPrice()) {
                                item.setItemRetailPrice(alteredRetailPrice);
                            }
                            item.setName("St. " + item.getName());
                            exit = true;
                        } else {
                            throw (new NumberFormatException());
                        }
                    } catch (NumberFormatException exception) {
                        response = view.inputStornoRetailPrice(item.getItemRetailPrice(), true);
                    }
                }
            } while (!exit);
        } else if (!piece && item.getRetailPrice() < 0) {
            result = (view.confirmStorno() == JOptionPane.YES_OPTION);
        }
        return result;
    }

    boolean addToShoppingCart() {
        //removed success variable sry for changing your code but I think it's not
        //good to have a return statement after the function already returned a value
        boolean piece = (view.getOption() == ShoppingMaskUIView.ARTICLE_NUMBER || view.getOption() == ShoppingMaskUIView.CUSTOM_PRODUCT);
        try {
            ShoppingItem item = extractShoppingItemFromUI();
            if (item.getItemMultiplier() != 0 && (view.getOption() == ShoppingMaskUIView.RETURN_DEPOSIT || checkStorno(item, piece) )) {
                shoppingCartController.addShoppingItem(item, piece);
                return true;
            }
            return false;
        } catch (UndefinedInputException undefinedInputException) {
            view.noArticleFound();
            return false;
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
            view.messageBarcodeNotFound(barcode);
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
                if (view.getDeposit() < 0) {
                    view.messageDepositStorno();
                    return null;
                } else {
                    return ShoppingItem.createDeposit(view.getDeposit());
                }
            case ShoppingMaskUIView.RETURN_DEPOSIT:
                if (view.getDeposit() < 0) {
                    view.messageDepositStorno();
                    return null;
                } else {
                    return ShoppingItem.createDeposit(view.getDeposit() * (-1));
                }
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
        new PayController(model.getSaleSession(), shoppingCartController.getItems(), () -> {
            getView().back();
        }, new Dimension(view.getShoppingListSize().width, view.getContent().getHeight())).openAsWindow(view.getWindow(), SubWindow::new);
    }

    void openSearchWindow() {
        new ArticleSelectorController(this::searchWindowResult).openAsWindow(view.getWindow(),SubWindow::new);
    }

    void searchWindowResult(Article article) {
        view.setOptArticleNo();
        view.loadItemStats(article);
    }

    void editUserAction() {
        new EditUserController(model.getSaleSession().getCustomer(), Mode.EDIT).openAsWindow(view.getWindow(),SubWindow::new);
    }

    public void processBarcode(String barcode) {
        try {
            long bc = Long.parseLong(barcode);
            searchByBarcode(bc);
        } catch (NumberFormatException e) {
            view.messageInvalidBarcode(barcode);
        }

    }
}
