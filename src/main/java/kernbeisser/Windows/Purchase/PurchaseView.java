package kernbeisser.Windows.Purchase;

import javax.swing.*;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartController;
import kernbeisser.CustomComponents.ShoppingTable.ShoppingCartView;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PurchaseView implements IView<PurchaseController> {
  private JButton finish;
  private JLabel date;
  private JLabel count;
  private JLabel seller;
  private JLabel customer;
  private JPanel main;
  private JLabel header;
  private JButton printBon;
  private JLabel countL;
  private JLabel customerL;
  private JLabel sellerL;
  private ShoppingCartView cartView;

  @Linked private PurchaseController controller;

  @Linked private ShoppingCartController cartController;

  void setDate(String date) {
    this.date.setText(date);
  }

  void setCustomer(String customerName) {
    customer.setText(customerName);
  }

  void setSeller(String sellerName) {
    seller.setText(sellerName);
  }

  void setItemCount(int c) {
    count.setText(c + "");
  }

  private void createUIComponents() {
    cartView = cartController.getView();
  }

  @Override
  public void initialize(PurchaseController controller) {
    finish.addActionListener((e) -> back());
    printBon.addActionListener(e -> controller.printBon());

    double scale = 1.5;
    Tools.scaleFonts(
        scale, count, seller, customer, main, printBon, countL, customerL, sellerL, finish);
    Tools.scaleFonts(scale * 2, date, header);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }
}
