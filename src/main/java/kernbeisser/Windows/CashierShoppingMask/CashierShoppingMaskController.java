package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import org.jetbrains.annotations.NotNull;

import javax.persistence.NoResultException;

public class CashierShoppingMaskController implements Controller<CashierShoppingMaskView,CashierShoppingMaskModel> {
    private final CashierShoppingMaskModel model;
    private final CashierShoppingMaskView view;

    private final SearchBoxController<User> searchBoxController;

    public CashierShoppingMaskController() {
        this.searchBoxController = new SearchBoxController<>(User::defaultSearch,
                                                             Column.create("Vorname", User::getFirstName,
                                                                           PermissionKey.USER_FIRST_NAME_READ),
                                                             Column.create("Nachname", User::getSurname,
                                                                           PermissionKey.USER_SURNAME_READ),
                                                             Column.create("Benutzername", User::getUsername,
                                                                           PermissionKey.USER_USERNAME_READ)
        );
        searchBoxController.initView();
        searchBoxController.addLostSelectionListener(() -> selectUser(null));
        searchBoxController.addSelectionListener(this::selectUser);
        model = new CashierShoppingMaskModel();
        this.view = new CashierShoppingMaskView(this);
    }


    private void selectUser(User user) {
        if (user != null) {
            view.setOpenShoppingMaskEnabled(true);
            view.setStartFor(user.toString());
        } else {
            view.setOpenShoppingMaskEnabled(false);
        }
    }

    public void openMaskWindow() {
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(searchBoxController.getSelectedObject());
        saleSession.setSeller(LogInModel.getLoggedIn());
        if (!view.getSecondSeller().toString().equals("Keiner")) {
            try {
                saleSession.setSecondSeller(view.getSecondSeller());
            } catch (NoResultException e) {
                return;
            }
        }
        new ShoppingMaskUIController(saleSession).openTab(
                "Einkauf für " + saleSession.getCustomer().getSurname() + ", " + saleSession.getCustomer()
                                                                                            .getFirstName());
    }

    public SearchBoxView<User> getSearchBoxView() {
        return searchBoxController.getView();
    }

    @Override
    public @NotNull CashierShoppingMaskView getView() {
        return view;
    }

    @Override
    public @NotNull CashierShoppingMaskModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
        view.setAllSecondarySellers(User.getAll(null));
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }
}
