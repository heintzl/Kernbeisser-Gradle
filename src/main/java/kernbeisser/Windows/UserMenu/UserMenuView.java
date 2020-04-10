package kernbeisser.Windows.UserMenu;

import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.UserInfo.UserInfoView;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class UserMenuView implements View<UserMenuController> {
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

    private final UserMenuController controller;

    public UserMenuView(UserMenuController controller) {
        this.controller = controller;
    }

    void setUsername(String s) {
        username.setText(s);
    }

    private void createUIComponents() {
        userInfoView = controller.getUserInfoView(this);
    }

    @Override
    public void initialize(UserMenuController controller) {
        startInventory.addActionListener(e -> controller.startInventory());
        showValueHistory.addActionListener(e -> controller.showValueHistory());
        showProfile.addActionListener(e -> controller.showProfile());
        beginCashierJob.addActionListener(e -> controller.beginCashierJob());
        logOut.addActionListener(e -> controller.logOut());
        beginSelfShopping.addActionListener(e -> controller.beginSelfShopping());
        orderContainer.addActionListener(e -> controller.orderContainers());
    }

    @Override
    public @NotNull Dimension getSize() {
        return new Dimension(900,600);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
