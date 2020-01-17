package kernbeisser.Windows.UserMenu;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntitys.Purchase;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

public class UserMenuView extends Window implements View {
    private ObjectTable<Purchase> buyHistory;
    private JButton beginSelfShopping;
    private JButton logOut;
    private JButton beginCashierJob;
    private JButton showProfile;
    private JButton showValueHistory;
    private JButton startInventory;
    private JLabel welcome;
    private JPanel main;

    private UserMenuController controller;

    UserMenuView(UserMenuController controller,Window current){
        super(current);
        this.controller = controller;
        startInventory.addActionListener(e -> controller.startInventory());
        showValueHistory.addActionListener(e -> controller.showValueHistory());
        showProfile.addActionListener(e -> controller.showProfile());
        beginCashierJob.addActionListener(e -> controller.beginCashierJob());
        logOut.addActionListener(e -> controller.logOut());
        beginSelfShopping.addActionListener(e -> controller.beginSelfShopping());
        add(main);
        setSize(900,600);
        setLocationRelativeTo(null);
    }

    Purchase getSelected(){
        return buyHistory.getSelectedObject();
    }

    void setBuyHistory(Collection<Purchase> purchases){
        buyHistory.setObjects(purchases);
    }

    void setUsername(String s){
        welcome.setText("Willkommen "+s);
    }

    private void createUIComponents() {
        buyHistory = new ObjectTable<>(
                Column.create("Datum", Purchase::getCreateDate),
                Column.create("Betrag", Purchase::getSum),
                Column.create("Ladendienst", e -> e.getSession().getSeller().getFirstName() + " " + e.getSession().getSeller().getSurname()),
                Column.create("", (e) -> new JButton(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent a) {
                        controller.showPurchase();
                    }
                })
        ));
        buyHistory.setComplex(true);
    }

    @Override
    public Controller getController() {
        return controller;
    }
}
