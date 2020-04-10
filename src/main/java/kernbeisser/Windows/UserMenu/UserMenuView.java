package kernbeisser.Windows.UserMenu;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.UserInfo.UserInfoView;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

public class UserMenuView extends Window implements View {
    private JButton beginSelfShopping;
    private JButton logOut;
    private JButton beginCashierJob;
    private JButton showProfile;
    private JButton showValueHistory;
    private JButton startInventory;
    private JPanel main;
    private JButton orderContainer;
    private UserInfoView userInfoView;
    private JLabel username;

    private UserMenuController controller;

    UserMenuView(UserMenuController controller, Window current) {
        super(current);
        this.controller = controller;
        startInventory.addActionListener(e -> controller.startInventory());
        showValueHistory.addActionListener(e -> controller.showValueHistory());
        showProfile.addActionListener(e -> controller.showProfile());
        beginCashierJob.addActionListener(e -> controller.beginCashierJob());
        logOut.addActionListener(e -> controller.logOut());
        beginSelfShopping.addActionListener(e -> controller.beginSelfShopping());
        orderContainer.addActionListener(e -> controller.orderContainers());
        add(main);
        setSize(900, 600);
        setLocationRelativeTo(null);
        windowInitialized();
    }

    void setUsername(String s) {
        username.setText(s);
    }

    private void createUIComponents() {
        userInfoView = controller.getUserInfoView(this);
    }

}
