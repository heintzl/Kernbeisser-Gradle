package kernbeisser.Windows.Purchase;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class PurchaseView implements View<PurchaseController> {
    private final PurchaseController controller;
    private JButton finish;
    private ObjectTable<ShoppingItem> items;
    private JLabel date;
    private JLabel sum;
    private JLabel count;
    private JLabel seller;
    private JLabel customer;
    private JPanel main;


    PurchaseView(PurchaseController controller) {
        this.controller = controller;
    }

    void setDate(String date) {
        this.date.setText(date);
    }

    void setCustomer(String customerName) {
        customer.setText(customerName);
    }

    void setSeller(String sellerName) {
        seller.setText(sellerName);
    }

    void setSum(double sum) {
        this.sum.setText(sum  + "€");
    }

    void setItemCount(int c) {
        count.setText(c + "");
    }

    void setItems(Collection<ShoppingItem> items) {
        this.items.setObjects(items);
    }

    private void createUIComponents() {
        items = new ObjectTable<ShoppingItem>(
                Column.create("Artikelname", ShoppingItem::getName),
                Column.create("Anzahl", ShoppingItem::getItemMultiplier),
                Column.create("Verkaufs Preis", e -> controller.getPrice(e)  + "€"),
                Column.create("Netto Preis", e -> controller.getPrice(e)  + "€")
        );
    }

    @Override
    public void initialize(PurchaseController controller) {
        finish.addActionListener((e) -> back());
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

}
