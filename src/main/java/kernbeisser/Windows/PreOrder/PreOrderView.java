package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PreOrderView implements IView<PreOrderController> {
  private kernbeisser.CustomComponents.PermissionButton add;
  private ObjectTable<PreOrder> preOrders;
  private IntegerParseField amount;
  private JLabel name;
  private JLabel containerSize;
  private JLabel sellingPrice;
  private JPanel main;
  private JPanel insertSection;
  private JLabel netPrice;
  private kernbeisser.CustomComponents.ComboBox.AdvancedComboBox<User> user;
  private IntegerParseField kkNumber;
  private JButton close;
  private JButton abhakplanButton;
  private JButton bestellungExportierenButton;

  @Linked private PreOrderController controller;

  void setInsertSectionEnabled(boolean b) {
    insertSection.setVisible(b);
  }

  int getAmount() {
    return amount.getSafeValue();
  }

  void setAmount(String s) {
    amount.setText(s);
  }

  int getKkNumber() {
    return kkNumber.getSafeValue();
  }

  void setKkNumber(int s) {
    kkNumber.setText(String.valueOf(s));
  }

  void setNetPrice(double s) {
    netPrice.setText(String.format("%.2f€", s));
  }

  private void createUIComponents() {
    preOrders =
        new ObjectTable<>(
            Column.create("Benutzer", e -> e.getUser().getFullName()),
            Column.create("Ladennummer", PreOrder::getKBNumber),
            Column.create("Kornkraftnummer", e -> e.getArticle().getSuppliersItemNumber()),
            Column.create("Produktname", e -> e.getArticle().getName()),
            Column.create(
                "Netto-Preis",
                e ->
                    String.format(
                        "%.2f€",
                        PreOrderModel.containerNetPrice(e.getArticle()), SwingConstants.RIGHT)),
            Column.create("Anzahl", PreOrder::getAmount),
            Column.createIcon(
                IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED), controller::delete));

    user = new AdvancedComboBox<>(User::getFullName);
  }

  void setPreOrders(Collection<PreOrder> preOrders) {
    this.preOrders.setObjects(preOrders);
  }

  void setItemName(String s) {
    name.setText(s);
  }

  void setContainerSize(String s) {
    containerSize.setText(s);
  }

  void setSellingPrice(String s) {
    sellingPrice.setText(s);
  }

  PreOrder getSelectedOrder() {
    return preOrders.getSelectedObject();
  }

  void noItemFound() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Es konnte kein Kornkraft-Artikel mit dieser Kornkraft-/Kernbeißer-Nummer gefunden werden.");
  }

  void resetArticleNr() {
    kkNumber.setText("");
    amount.setText("1");
    kkNumber.requestFocusInWindow();
  }

  @Override
  public void initialize(PreOrderController controller) {
    kkNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (controller.searchKK()) {
              if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                amount.selectAll();
                amount.requestFocusInWindow();
              }
            }
          }
        });

    user.addActionListener(e -> userAction(false));

    add.addActionListener(e -> controller.add());

    preOrders.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) controller.delete(getSelectedOrder());
          }
        });

    amount.addActionListener(e -> controller.add());
    abhakplanButton.addActionListener(e -> controller.printChecklist());

    close.addActionListener(e -> back());
  }

  private void userAction(boolean fromFnKey) {
    if (!fromFnKey) enableControls(true);
    if (controller.searchKK()) {
      amount.requestFocusInWindow();
      if (fromFnKey) controller.add();
    } else {
      kkNumber.requestFocusInWindow();
    }
  }

  void fnKeyAction(String i) {
    setAmount(i);
    userAction(true);
  }

  void enableControls(boolean active) {
    kkNumber.setEnabled(active);
    amount.setEnabled(active);
    add.setEnabled(active);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Vorbestellung";
  }

  public User getUser() {
    return (User) user.getSelectedItem();
  }

  public void addPreOrder(PreOrder order) {
    preOrders.add(order);
  }

  public void noPreOrderSelected() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Bitte wählen sie vorher eine Vorbestellung aus!");
  }

  public void remove(PreOrder selected) {
    preOrders.remove(selected);
  }

  public void setUser(Collection<User> allUser) {
    user.removeAllItems();
    allUser.forEach(user::addItem);
  }

  public void noArticleFoundForBarcode(String barcode) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Konnte keinen Kornkraft-Artikel mit Barcode \"" + barcode + "\" finden",
        "Artikel nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }
}
