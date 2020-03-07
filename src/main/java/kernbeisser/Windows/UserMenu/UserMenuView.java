package kernbeisser.Windows.UserMenu;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.swing.*;
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
    private JButton orderContainer;

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
    }

    Purchase getSelected() {
        return buyHistory.getSelectedObject();
    }

    void setBuyHistory(Collection<Purchase> purchases) {
        buyHistory.setObjects(purchases);
    }

    void setUsername(String s) {
        welcome.setText("Willkommen " + s);
    }

    private void createUIComponents() {
        buyHistory = new ObjectTable<>(
                Column.create("Datum", Purchase::getCreateDate),
                Column.create("Betrag", e -> e.getSum() / 100f + "€"),
                Column.create("Ladendienst", e -> e.getSession().getSeller().getFirstName() + " " + e.getSession()
                                                                                                     .getSeller()
                                                                                                     .getSurname()),
                Column.create("Anschauen", (e) -> "Anschauen", (e) -> controller.showPurchase())
        );
        //buyHistory.setComplex(true);
    }

}
