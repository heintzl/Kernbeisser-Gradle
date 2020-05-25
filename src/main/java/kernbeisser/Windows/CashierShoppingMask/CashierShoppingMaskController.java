package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import org.jetbrains.annotations.NotNull;

public class CashierShoppingMaskController implements Controller<CashierShoppingMaskView,CashierShoppingMaskModel> {
    private CashierShoppingMaskModel model;
    private CashierShoppingMaskView view;

    private SearchBoxController<User> searchBoxController;

    public CashierShoppingMaskController() {
        this.searchBoxController = new SearchBoxController<User>(User::defaultSearch,
                                                                 Column.create("Vorname", User::getFirstName, Key.USER_FIRST_NAME_READ),
                                                             Column.create("Nachname", User::getSurname, Key.USER_SURNAME_READ),
                                                             Column.create("Benutzername", User::getUsername, Key.USER_USERNAME_READ)
        ){
            @Override
            public void refreshLoadSolutions() {
                CashierShoppingMaskController.this.selectUser(null);
                super.refreshLoadSolutions();
            }
        };
        searchBoxController.initView();
        searchBoxController.addSelectionListener(this::selectUser);
        model = new CashierShoppingMaskModel();
        this.view = new CashierShoppingMaskView(this);

    }

    private void selectUser(User user){
        if(user!=null){
            view.setOpenShoppingMaskEnabled(true);
            view.setStartFor(user.getUsername());
        }else
        view.setOpenShoppingMaskEnabled(false);
    }

    public void openMaskWindow() {
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(searchBoxController.getSelectedObject());
        saleSession.setSeller(LogInModel.getLoggedIn());
        view.addShoppingMaskView("Einkauf für "+saleSession.getCustomer().getUsername(),new ShoppingMaskUIController(saleSession).getInitializedView());
    }

    public SearchBoxView<User> getSearchBoxView(){
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

    }

    @Override
    public Key[] getRequiredKeys() {
        return new Key[0];
    }
}
