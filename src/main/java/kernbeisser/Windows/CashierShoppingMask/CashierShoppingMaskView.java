package kernbeisser.Windows.CashierShoppingMask;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.CashierMenu.CashierMenuController;
import kernbeisser.Windows.ShoppingMask.ShoppingMaskUIView;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class CashierShoppingMaskView extends Window{
    private JPanel main;
    private JTabbedPane tabbedPane;
    private SearchBoxView<User> searchBoxView;
    private PermissionButton openShoppingMask;

    private CashierShoppingMaskController controller;

    CashierShoppingMaskView(CashierShoppingMaskController controller, Window window){
        super(window);
        this.controller = controller;
        openShoppingMask.addActionListener(e -> controller.openMaskWindow());
        setSize(500,600);
        add(main);
        windowInitialized();
    }

    private void createUIComponents() {
        searchBoxView =  controller.getSearchBoxView();
    }

    void setSearchBoxView(SearchBoxView<User> userSearchBoxView){
        this.searchBoxView = userSearchBoxView;
    }

    void addShoppingMaskView(String title,ShoppingMaskUIView view){
        this.tabbedPane.addTab(title,view);
        pack();
        setLocationRelativeTo(null);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
    }

    public void setStartFor(String username) {
        openShoppingMask.setText("Einkauf für "+username+" beginnen");
    }

    public void setOpenShoppingMaskEnabled(boolean b) {
        openShoppingMask.setEnabled(b);
    }
}
