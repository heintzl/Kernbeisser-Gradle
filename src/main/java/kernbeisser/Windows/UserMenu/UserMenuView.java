package kernbeisser.Windows.UserMenu;

import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
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
    private JButton editPermissions;

    private final UserMenuController controller;

    public UserMenuView(UserMenuController controller) {
        this.controller = controller;
    }

    void setUsername(String s) {
        username.setText(s);
    }

    private void createUIComponents() {
        userInfoView = controller.getUserInfoView();
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
        editPermissions.addActionListener(e -> controller.openEditPermissionsWindow());
    }

    @Override
    public @NotNull Dimension getSize() {
        return new Dimension(900,600);
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    @Override
    public IconCode getTabIcon() {
        return FontAwesome.HOME;
    }
}
