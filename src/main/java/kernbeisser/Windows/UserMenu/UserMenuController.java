package kernbeisser.Windows.UserMenu;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.CashierMenu.CashierMenuController;
import kernbeisser.Windows.Container.ContainerController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.PermissionManagement.PermissionController;
import kernbeisser.Windows.TabbedPanel.TabbedPaneModel;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.UserInfo.UserInfoView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class UserMenuController implements Controller<UserMenuView,UserMenuModel> {
    private UserMenuView view;
    private UserMenuModel model;

    public UserMenuController() {
        this.model = new UserMenuModel();
        this.view = new UserMenuView(this);
    }

    @Override
    public @NotNull UserMenuView getView() {
        return view;
    }

    @Override
    public @NotNull UserMenuModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setUsername(LogInModel.getLoggedIn().getFirstName()+" "+LogInModel.getLoggedIn().getSurname());
    }

    @Override
    public boolean commitClose() {
        if (JOptionPane.showConfirmDialog(getView().getTopComponent(), "Sind sie Sicher das sie sich Ausloggen und\ndamit alle geöfnteten Tabs / Fenster schließen wollen") == 0) {
            TabbedPaneModel.DEFAULT_TABBED_PANE.clear();
            new SimpleLogInController().openTab("Log In");
            return true;
        }
        return false;
    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void beginSelfShopping() {
        new SoloShoppingMaskController().openTab("Einkaufsmaske");
    }

    public void logOut() {
        view.back();
    }

    public void beginCashierJob() {
        new CashierMenuController(model.getOwner()).openTab("Ladendienst Menu");
    }

    public void showProfile() {
    }

    public void showValueHistory() {
    }

    public void startInventory() {

    }

    public void orderContainers() {
        new ContainerController(model.getOwner()).openTab("Gebinde bestellen");
    }

    public UserInfoView getUserInfoView() {
        return new UserInfoController(model.getOwner()).getInitializedView();
    }

    public void openEditPermissionsWindow() {
        new PermissionController().openTab("Berechtigungen bearbeiten");
    }
}
