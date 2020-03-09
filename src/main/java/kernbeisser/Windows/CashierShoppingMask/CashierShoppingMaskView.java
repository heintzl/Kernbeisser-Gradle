package kernbeisser.Windows.CashierShoppingMask;

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Key;
import kernbeisser.Windows.CashierMenu.CashierMenuController;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class CashierShoppingMaskView extends Window{
    private JButton start;
    private JButton cancel;
    private ObjectTable<User> users;
    private JButton search;
    private JTextField searchBox;
    private JPanel main;


    CashierShoppingMaskView(CashierShoppingMaskController controller, Window window){
        super(window);
        add(main);
        setSize(500,600);
        start.addActionListener(e -> controller.openMaskWindow());
        cancel.addActionListener(e -> back());
        search.addActionListener(e -> controller.refresh());
        users.addSelectionListener(e -> controller.select());
        search.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH,15,new Color(0x757EFF)));
        searchBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.refresh();
            }
        });
    }

    private void createUIComponents() {
        users = new ObjectTable<User>(
                Column.create("Benutzername", User::getUsername, Key.USER_USERNAME_READ),
                Column.create("Vorname",User::getFirstName,Key.USER_FIRST_NAME_READ),
                Column.create("Nachname",User::getSurname,Key.USER_SURNAME_READ)
                );
    }

    void setUsers(Collection<User> users){
        this.users.setObjects(users);
    }

    User getSelectedUser(){
        return users.getSelectedObject();
    }

    void setEnable(boolean b){
        start.setEnabled(b);
        if(!b)start.setText("Nutzer auswählen");
    }

    void setTarget(String target){
        start.setText("Einkauf für "+target+" beginnen");
    }

    public String getSearch() {
        return searchBox.getText();
    }
}
