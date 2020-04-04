package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.Window;

import java.awt.event.ActionListener;

public class CashierShoppingMaskController{
    private CashierShoppingMaskModel model;
    private CashierShoppingMaskView view;
    public CashierShoppingMaskController(Window view) {
        this.view = new CashierShoppingMaskView(this,view);
        model = new CashierShoppingMaskModel();
        refresh();
    }

    public void refresh() {
        view.setEnable(false);
        view.setUsers(model.getUsers(view.getSearch()));
    }

    public void openMaskWindow() {
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(view.getSelectedUser());
        saleSession.setSeller(LogInModel.getLoggedIn());
        new ShoppingMaskUIController(null,saleSession);
        view.setEnable(false);
    }

    public void select() {
        view.setEnable(true);
        view.setTarget(view.getSelectedUser().getUsername());
    }
}
