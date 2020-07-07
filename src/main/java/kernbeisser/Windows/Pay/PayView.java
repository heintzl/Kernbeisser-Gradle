package kernbeisser.Windows.Pay;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.Window;
import kernbeisser.Windows.View;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.print.PrintService;
import javax.swing.*;
import java.util.Collection;
import java.util.List;

public class PayView implements View<PayController> {
    private final PayController controller;
    private JPanel main;
    private JPanel shoppingListPanel;
    private ShoppingCartView shoppingCartView;

    private JCheckBox printReceipt;
    private JButton commitPayment;
    private JButton cancel;
    private ShoppingCartController shoppingCartController;


    public PayView(Window current, PayController payController, ShoppingCartController cartController) {
        this.controller = payController;
        this.shoppingCartController = cartController;
    }

    private void createUIComponents() {
        shoppingCartView = shoppingCartController.getInitializedView();
    }

    public void fillShoppingCart(List<ShoppingItem> items) {
        items.forEach(e -> shoppingCartController.addShoppingItem(e,false));
    }

    @Override
    public void initialize(PayController controller) {
        commitPayment.addActionListener(e -> {
            controller.commitPayment(printReceipt.isSelected());
        });
        cancel.addActionListener(e -> {
            this.back();
        });
        shoppingCartController.fillUI();
    }

    @Override
    public @NotNull JComponent getContent() {
        return main;
    }

    public void notEnoughValue() {
        JOptionPane.showMessageDialog(getTopComponent(), "Sie haben nicht die Berechtigung unter das minimale Guthaben von " + String.format("%.2f€",Setting.DEFAULT_MIN_VALUE.getDoubleValue()) +" zu gehen");
    }
}
