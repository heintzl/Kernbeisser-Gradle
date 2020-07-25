package kernbeisser.Windows.Pay;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

public class PayView extends JPanel implements View<PayController> {
  private JPanel main;
  private JPanel shoppingListPanel;
  private ShoppingCartView shoppingCartView;

  private JCheckBox printReceipt;
  private JButton commitPayment;
  private JButton cancel;
  private final ShoppingCartController shoppingCartController;

  public PayView(ShoppingCartController cartController) {
    this.shoppingCartController = cartController;
  }

  private void createUIComponents() {
    shoppingCartView = shoppingCartController.getInitializedView();
  }

  public void fillShoppingCart(List<ShoppingItem> items) {
    double sum = 0;
    for (ShoppingItem item : items) {
      sum += item.getRetailPrice();
    }

    shoppingCartController.getView().setSum(sum);
    shoppingCartController.getView().setObjects(items);
  }

  void setViewSize(Dimension size) {
    this.setSize(size);
  }

  @Override
  public void initialize(PayController controller) {
    printReceipt.setSelected(true);
    commitPayment.addActionListener(
        e -> {
          controller.commitPayment(printReceipt.isSelected());
        });
    cancel.addActionListener(
        e -> {
          this.back();
        });
    shoppingCartController.fillUI();
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void notEnoughValue() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Sie haben nicht die Berechtigung unter das minimale Guthaben von "
            + String.format("%.2f€", Setting.DEFAULT_MIN_VALUE.getDoubleValue())
            + " zu gehen");
  }
}
