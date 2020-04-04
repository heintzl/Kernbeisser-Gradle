package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIController;
import kernbeisser.Windows.Window;

public class CashierShoppingMaskController{
    private CashierShoppingMaskModel model;
    private CashierShoppingMaskView view;

    private SearchBoxController<User> searchBoxController;

    public CashierShoppingMaskController(Window current) {
        this.searchBoxController = new SearchBoxController<User>(User::defaultSearch, this::selectUser,
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
        model = new CashierShoppingMaskModel();
        this.view = new CashierShoppingMaskView(this,current);
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
        view.addShoppingMaskView("Einkauf für "+saleSession.getCustomer().getUsername(),new ShoppingMaskUIController(saleSession).getView());
    }

    public SearchBoxView<User> getSearchBoxView(){
        return searchBoxController.getView();
    }
}
