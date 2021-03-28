package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
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
  JButton abhakplanButton;
  JButton bestellungExportierenButton;
  JButton searchArticle;

  private JPopupMenu popupSelectionColumn;
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
    Icon selected = IconFontSwing.buildIcon(FontAwesome.CHECK_SQUARE, 20, new Color(0x38FF00));
    Icon unselected = IconFontSwing.buildIcon(FontAwesome.SQUARE, 20, new Color(0xC7C7C7));
    if (!controller.restrictToLoggedIn) {
      JMenuItem popupSelectAll = new JMenuItem("alle auswählen");
      popupSelectAll.addActionListener(e -> setAllDelivered(true));
      JMenuItem popupDeselectAll = new JMenuItem("alle abwählen");
      popupDeselectAll.addActionListener(e -> setAllDelivered(false));
      popupSelectionColumn = new JPopupMenu();
      popupSelectionColumn.add(popupSelectAll);
      popupSelectionColumn.add(popupDeselectAll);
    }
    preOrders =
        new ObjectTable<PreOrder>(
            Column.create("Benutzer", e -> e.getUser().getFullName()),
            Column.create("Ladennummer", PreOrder::getKBNumber, SwingConstants.RIGHT),
            Column.create(
                "Kornkraftnummer",
                e -> e.getArticle().getSuppliersItemNumber(),
                SwingConstants.RIGHT),
            Column.create("Produktname", e -> e.getArticle().getName()),
            Column.create(
                "Netto-Preis",
                e -> String.format("%.2f€", PreOrderModel.containerNetPrice(e.getArticle())),
                SwingConstants.RIGHT),
            Column.create(
                "Anzahl", PreOrder::getAmount, SwingConstants.CENTER, true, controller::editAmount),
            Column.createIcon(
                IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED), controller::delete));
    if (!controller.restrictToLoggedIn)
      preOrders.addColumnAtIndex(
          0,
          Column.createIcon(
              "ausgeliefert",
              e -> controller.isDelivered(e) ? selected : unselected,
              controller::toggleDelivery,
              e -> showSelectionPopup(),
              100));
    user = new AdvancedComboBox<>(User::getFullName);
  }

  private void showSelectionPopup() {
    Point mousePosition = preOrders.getMousePosition();
    popupSelectionColumn.show(preOrders, mousePosition.x, mousePosition.y);
  }

  void setAllDelivered(boolean allDelivered) {
    controller.setAllDelivered(allDelivered);
    popupSelectionColumn.setVisible(false);
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

  Collection<PreOrder> getSelectedOrders() {
    return preOrders.getSelectedObjects();
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

  void repaintTable() {
    preOrders.repaint();
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
            if (e.getKeyCode() == KeyEvent.VK_DELETE) controller.delete(getSelectedOrders());
          }
        });

    amount.addActionListener(e -> controller.add());
    abhakplanButton.addActionListener(e -> controller.printChecklist());
    searchArticle.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 20, new Color(49, 114, 128)));
    bestellungExportierenButton.addActionListener(e -> controller.exportPreOrder());
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

  void enableControls(boolean enabled) {
    searchArticle.setEnabled(enabled);
    kkNumber.setEnabled(enabled);
    amount.setEnabled(enabled);
    add.setEnabled(enabled);
  }

  public User getUser() {
    return (User) user.getSelectedItem();
  }

  public void addPreOrder(PreOrder order) {
    preOrders.add(order);
  }

  public void refreshPreOrder(PreOrder order) {
    preOrders.replace(order, order);
  }

  public void noPreOrderSelected() {
    JOptionPane.showMessageDialog(getTopComponent(), "Bitte wähle vorher eine Vorbestellung aus!");
  }

  public void remove(PreOrder selected) {
    preOrders.remove(selected);
  }

  public void setUsers(Collection<User> allUser) {
    user.setItems(allUser);
    if (user.getModel().getSize() > 1) {
      user.setSelectedItem(null);
    }
    enableControls(controller.restrictToLoggedIn);
  }

  public void noArticleFoundForBarcode(String barcode) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Konnte keinen Kornkraft-Artikel mit Barcode \"" + barcode + "\" finden",
        "Artikel nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageExportError(Throwable e) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Der Export ist fehlgeschlagen. Ursache: \n" + e.getMessage(),
        "Vorbestellungsexport",
        JOptionPane.ERROR_MESSAGE);
  }

  public void messageExportSuccess() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Die Vorbestellung wurde erfolgreich exportiert",
        "Vorbestellungsexport",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageExportCanceled() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Der Export der Vorbestellung wurde abgebrochen",
        "Vorbestellungsexport",
        JOptionPane.WARNING_MESSAGE);
  }

  public void notifyNoUserSelected() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Die Vorbestellung kann nicht aufgenommen werden,"
            + "\nda der Nutzer noch nicht ausgewählt wurde."
            + "\nBitte wählen sie zunächst einen Benutzer aus,"
            + "\nauf dessen Namen die Vorbestellung ausgeführt werden soll.",
        "Kein Benutzer ausgewählt",
        JOptionPane.WARNING_MESSAGE);
  }

  public String inputAmount(int amount, boolean retry) {
    String initValue = MessageFormat.format("{0, number, 0}", amount).trim();
    String message = "";
    String response = "";
    if (retry) { // item is piece, first try
      message = "Die Eingabe ist ungültig. Bitte hier eine gültige Anzahl > 0 eingeben:";
    } else { // item is piece later try
      message = "Bitte neue Anzahl eingeben:";
    }
    Tools.beep();
    response =
        (String)
            JOptionPane.showInputDialog(
                getContent(),
                message,
                "Anzahl anpassen",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                initValue);
    if (response != null) {
      response = response.trim();
    }
    return response;
  }

  public void setUserEnabled(boolean enabled) {
    user.setEnabled(enabled);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return (controller.restrictToLoggedIn ? "Meine " : "") + "Vorbestellung";
  }
}
