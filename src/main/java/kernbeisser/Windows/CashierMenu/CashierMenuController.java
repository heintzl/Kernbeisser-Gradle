package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMask;
import kernbeisser.Windows.CatalogInput.CatalogInput;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.ManagePriceLists.ManagePriceLists;
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
        new ManageUser(model.getUser().getPermission())   {
            @Override
            public void finish() {
                dispose();
                view.open();
            }
        };
        view.close();
    }
    public void openManageUsers(){
        new ManageUser(model.getUser().getPermission())   {
            @Override
            public void finish() {
                dispose();
                view.open();
            }
        };
        view.close();
    }
    public void openManagePriceLists(){
        new ManagePriceLists()  {
            @Override
            public void finish() {
                dispose();
                view.open();
            }
        };
        view.close();
    }
    public void openCashierMask(){
        new CashierShoppingMask(model.getUser()) {
            @Override
            public void finish() {
                view.open();
            }
        };
        view.close();
    }
    public void openCatalogInput(){
        new CatalogInput() {
            @Override
            public void finish() {
                dispose();
                view.open();
            }
        };
        view.close();
    }
}
