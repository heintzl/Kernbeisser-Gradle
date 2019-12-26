package kernbeisser.Windows.UserMenu;

import kernbeisser.DBEntitys.SaleSession;
import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.CashierMenu.CashierMenuView;
import kernbeisser.Windows.Finisher;
import kernbeisser.Windows.InventoryMenu.InventoryMenuView;
import kernbeisser.Windows.LogIn.LogInView;
import kernbeisser.Windows.Options.Options;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskView;
import kernbeisser.Windows.Stats.Stats;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;

public class UserMenuController {
    private UserMenuView view;
    private UserMenuModel model;
    UserMenuController(UserMenuView view,User owner){
        this.view=view;
        this.model=new UserMenuModel(owner);
    }
    void startSoloShopping(){
        Window jFrame = new Window(view);
        jFrame.setLayout(new GridLayout(1,1));
        SaleSession saleSession = new SaleSession();
        saleSession.setCustomer(model.getOwner());
        saleSession.setSeller(model.getOwner());
        jFrame.add(new ShoppingMaskView(jFrame, saleSession));
        jFrame.addWindowListener(new Finisher(() -> {
            jFrame.dispose();
            view.open();
        }));
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
    void openStats(){
        new Stats(view);
    }
    void startCashierAction() {
        new CashierMenuView(model.getOwner(), view);
    }

    void logOutAction() {
        new LogInView(null);
        view.back();
    }

    void startInventoryAction() {
        new InventoryMenuView(view);
    }

    void startOptionsAction() {
        new Options(view);
    }

}
