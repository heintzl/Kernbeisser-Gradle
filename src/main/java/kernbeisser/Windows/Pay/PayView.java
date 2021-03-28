package kernbeisser.Windows.Pay;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class PayView implements IView<PayController> {
  private JPanel main;
  private JPanel shoppingListPanel;
  private ShoppingCartView shoppingCartView;

  JCheckBox printReceipt;
  private JButton commitPayment;
  private JButton cancel;
  JButton setCustomerStandard;
  private PayController controller;

  @Linked private ShoppingCartController shoppingCartController;

  private void createUIComponents() {
    shoppingCartView = shoppingCartController.getView();
  }

  public void fillShoppingCart(List<ShoppingItem> items) {
    double sum = 0;
    for (ShoppingItem item : items) {
      sum += item.getRetailPrice();
    }

    var view = shoppingCartController.getView();
    view.setSum(sum);
    view.setValue(controller.getUserValue() - sum);
    view.setObjects(items);
  }

  public void confirmLogging(String name, double value) {
    if (JOptionPane.showConfirmDialog(
            getTopComponent(),
            String.format(
                "Ist der Einkauf in Höhe von %.2f€ von %s in das Log-Buch eintragen worden?",
                value, name),
            "Log-Bucheintrag",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        != 0) {
      JOptionPane.showMessageDialog(
          getTopComponent(),
          "Alle Einkäufe müssen im Log-Buch notiert werden\n"
              + "für den Fall, dass gespeicherte Daten verloren gehen.");
      confirmLogging(name, value);
    }
  }

  void setViewSize(Dimension size) {
    main.setSize(size);
  }

  @Override
  public void initialize(PayController controller) {
    this.controller = controller;
    printReceipt.setSelected(true);
    commitPayment.addActionListener(
        e -> {
          controller.commitPayment(printReceipt.isSelected());
        });
    cancel.addActionListener(
        e -> {
          this.back();
        });
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void notEnoughValue() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Du hast nicht die Berechtigung unter das minimale Guthaben von "
            + String.format("%.2f€", Setting.DEFAULT_MIN_VALUE.getDoubleValue())
            + " zu gehen.");
  }

  @Override
  public @NotNull Dimension getSize() {
    return Tools.floatingSubwindowSize(controller);
  }
}
