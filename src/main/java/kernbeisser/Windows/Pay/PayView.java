package kernbeisser.Windows.Pay;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.View;
import kernbeisser.Windows.Window;

import javax.print.PrintService;
import javax.swing.*;
import java.util.Collection;

class PayView extends Window implements View {
    private final PayController controller;
    private JPanel main;
    private JButton commitPayment;
    private JRadioButton printBon;
    private JRadioButton printNoBon;
    private JComboBox paperFormat;
    private JComboBox<PrintService> printers;
    private JButton cancel;
    private ObjectTable<ShoppingItem> shoppingCart;


    public PayView(Window current, PayController payController) {
        super(current);
        this.controller = payController;

        add(main);
        pack();
        setLocationRelativeTo(current);
        commitPayment.addActionListener(e -> {
            payController.commitPayment();
        });
        cancel.addActionListener(e -> {
            this.back();
        });
    }

    PrintService getSelectedPrintService() {
        return printers.getItemAt(printers.getSelectedIndex());
    }

    void setSelectedPrintService(PrintService printService) {
        printers.setSelectedItem(printService);
    }

    void setPrintServices(PrintService[] printServices) {
        printers.removeAllItems();
        for (PrintService service : printServices) {
            printers.addItem(service);
        }
    }

    void fillShoppingCart(Collection<ShoppingItem> items) {
        shoppingCart.setObjects(items);
    }

    private void createUIComponents() {
        shoppingCart = new ObjectTable<>(
                Column.create("Name", ShoppingItem::getName),
                Column.create("Anzahl", ShoppingItem::getItemMultiplier),
                Column.create("Preis", e -> controller.getPrice(e) / 100f + "€")
        );
    }


}
