package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.CatalogInput.CatalogInputController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditItems.EditItems;
import kernbeisser.Windows.EditSurchargeTables.EditSurchargeTables;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.Trasaction.TransactionController;
import org.jetbrains.annotations.NotNull;

public class CashierMenuController implements Controller<CashierMenuView,CashierMenuModel> {
    private CashierMenuModel model;
    private CashierMenuView view;

    public CashierMenuController( User user) {
        this.view = new CashierMenuView( this);
        model = new CashierMenuModel(user);
    }

    @Override
    public @NotNull CashierMenuView getView() {
        return view;
    }

    @Override
    public @NotNull CashierMenuModel getModel() {
        return model;
    }

    @Override
    public void fillUI() { }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void openManageItems() {
        new EditItems().openAsWindow(view.getWindow(), JFrameWindow::new);
    }

    public void openManageSurchargeTables() {
        new EditSurchargeTables().openAsWindow(getView().getWindow(),JFrameWindow::new);
    }

    public void openManageUsers() {
        new EditUsers().openAsWindow(getView().getWindow(),JFrameWindow::new);
    }

    public void openManagePriceLists() {
        new ManagePriceListsController();
    }

    public void openCashierMask() {
        new CashierShoppingMaskController().openAsWindow(view.getWindow(),JFrameWindow::new);
    }

    public void openCatalogInput() {
        new CatalogInputController().openAsWindow(view.getWindow(),JFrameWindow::new);
    }

    public void openTransfer() {
        new TransactionController(model.getUser()).openAsWindow(view.getWindow(),JFrameWindow::new);
    }
}
