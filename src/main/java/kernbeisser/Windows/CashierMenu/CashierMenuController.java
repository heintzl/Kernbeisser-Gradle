package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskView;
import kernbeisser.Windows.CatalogInput.CatalogInputView;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.ManageItems.ManageItemsView;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsView;
import kernbeisser.Windows.ManageUser.ManageUser;

class CashierMenuController implements Controller {
    private CashierMenuModel model;
    private CashierMenuView view;

    CashierMenuController(User user,CashierMenuView view){
        this.view=view;
        model=new CashierMenuModel(user);
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

    public void openManageItems(){
        new ManageItemsView(view);
    }
    public void openManageUsers(){
        new ManageUser(view,model.getUser().getPermission());
    }
    public void openManagePriceLists(){
        new ManagePriceListsView(view);
    }
    public void openCashierMask(){
        new CashierShoppingMaskView(model.getUser(),view).open();
    }
    public void openCatalogInput(){
        new CatalogInputView(view);
    }
}
