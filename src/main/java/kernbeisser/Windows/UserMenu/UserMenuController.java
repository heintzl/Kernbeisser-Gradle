package kernbeisser.Windows.UserMenu;

import kernbeisser.DBEntities.SaleSession;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.*;
import kernbeisser.Windows.CashierMenu.CashierMenuController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskView;
import kernbeisser.Windows.Window;

import java.awt.*;

public class UserMenuController implements Controller {
    private UserMenuView view;
    private UserMenuModel model;
    public UserMenuController(Window current, User owner){
        this.view=new UserMenuView(this,current);
        this.model=new UserMenuModel(owner);
        view.setUsername(owner.getUsername());
        view.setBuyHistory(model.getAllPurchase());
    }


    public void showPurchase() {

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }

    public void beginSelfShopping() {
        Window jFrame = new Window(view);
        jFrame.setLayout(new GridLayout(1,1));
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(model.getOwner());
        saleSession.setSeller(model.getOwner());
        jFrame.add(new ShoppingMaskView(jFrame, saleSession));
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    public void logOut() {
        view.back();
    }

    public void beginCashierJob() {
        new CashierMenuController(view,model.getOwner());
    }

    public void showProfile() {
    }

    public void showValueHistory() {
    }

    public void startInventory() {

    }
}
