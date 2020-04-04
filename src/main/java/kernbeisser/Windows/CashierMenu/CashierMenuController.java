package kernbeisser.Windows.CashierMenu;

import kernbeisser.CustomComponents.DatePicker.DatePickerView;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.CatalogInput.CatalogInputController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditItems.EditItems;
import kernbeisser.Windows.EditSurchargeTables.EditSurchargeTables;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsView;
import kernbeisser.Windows.Trasaction.TransactionController;
import kernbeisser.Windows.Window;

public class CashierMenuController implements Controller {
    private CashierMenuModel model;
    private CashierMenuView view;

    public CashierMenuController(Window current, User user) {
        this.view = new CashierMenuView(this, current);
        model = new CashierMenuModel(user);
    }

    @Override
    public void refresh() {

    }

    @Override
    public CashierMenuView getView() {
        return view;
    }

    @Override
    public CashierMenuModel getModel() {
        return model;
    }

    public void openManageItems() {
        new EditItems(this.getView());
    }

    public void openManageSurchargeTables() {
        new EditSurchargeTables(getView());
    }

    public void openManageUsers() {
        new EditUsers(getView());
    }

    public void openManagePriceLists() {
        new ManagePriceListsView(view);
    }

    public void openCashierMask() {
        new CashierShoppingMaskController(view);
    }

    public void openCatalogInput() {
        new CatalogInputController(view);
    }

    public void openTransfer() {
        new TransactionController(view, model.getUser());
    }
}
