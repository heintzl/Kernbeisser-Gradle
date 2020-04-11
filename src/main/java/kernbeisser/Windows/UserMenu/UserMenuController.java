package kernbeisser.Windows.UserMenu;

import kernbeisser.Enums.Key;
import kernbeisser.Windows.CashierMenu.CashierMenuController;
import kernbeisser.Windows.Container.ContainerController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.SoloShoppingMask.SoloShoppingMaskController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import kernbeisser.Windows.UserInfo.UserInfoView;
import org.jetbrains.annotations.NotNull;

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
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void beginSelfShopping() {
        new SoloShoppingMaskController().openAsWindow(view.getWindow(),JFrameWindow::new);
    }

    public void logOut() {
        view.back();
    }

    public void beginCashierJob() {
        new CashierMenuController(model.getOwner()).openAsWindow(view.getWindow(),JFrameWindow::new);
    }

    public void showProfile() {
    }

    public void showValueHistory() {
    }

    public void startInventory() {

    }

    public void orderContainers() {
        new ContainerController(model.getOwner()).openAsWindow(view.getWindow(), JFrameWindow::new);
    }

    public UserInfoView getUserInfoView() {
        return new UserInfoController(model.getOwner()).getInitializedView();
    }
}
