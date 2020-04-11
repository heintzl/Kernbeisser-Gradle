package kernbeisser.Windows.Pay;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.JFrameWindow;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

import javax.print.PrintService;
import javax.swing.*;
import java.util.Collection;

public class PayView implements View<PayController> {
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
        this.controller = payController;
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
                Column.create("Preis", e -> controller.getPrice(e)  + "€")
        );
    }


    @Override
    public void initialize(PayController controller) {
        commitPayment.addActionListener(e -> {
            controller.commitPayment();
        });
        cancel.addActionListener(e -> {
            this.back();
        });
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }
}
