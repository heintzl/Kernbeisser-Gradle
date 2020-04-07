package kernbeisser.Windows.UserMenu;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Windows.*;
import kernbeisser.Windows.CashierMenu.CashierMenuController;
import kernbeisser.Windows.Container.ContainerController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Purchase.PurchaseController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskView;
import kernbeisser.Windows.Window;

public class UserMenuController implements Controller {
    private UserMenuView view;
    private UserMenuModel model;

    public UserMenuController(Window current) {
        this.view = new UserMenuView(this, current);
        this.model = new UserMenuModel();
        view.setUsername(LogInModel.getLoggedIn().getUsername());
        view.setBuyHistory(model.getAllPurchase());
    }


    public void showPurchase() {
        new PurchaseController(view, view.getSelected());
    }

    @Override
    public UserMenuView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public void beginSelfShopping() {
        new SoloShoppingMaskController(view);
    }

    public void logOut() {
        view.back();
    }

    public void beginCashierJob() {
        new CashierMenuController(view, model.getOwner());
    }

    public void showProfile() {
    }

    public void showValueHistory() {
    }

    public void startInventory() {

    }

    public void orderContainers() {
        new ContainerController(view, model.getOwner());
    }
}
