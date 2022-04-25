package kernbeisser.Windows.PreOrder;

import com.github.lgooddatepicker.components.DatePicker;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.Locale;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.Columns.CustomizableColumn;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PreOrderView implements IView<PreOrderController> {

  private PermissionButton add;
  private ObjectTable<PreOrder> preOrders;
  private IntegerParseField amount;
  private JLabel name;
  private JLabel containerSize;
  private JLabel sellingPrice;
  private JPanel main;
  private JPanel insertSection;
  private JLabel netPrice;
  private AdvancedComboBox<User> user;
  private IntegerParseField kkNumber;
  private JButton close;
  JButton abhakplanButton;
  JButton bestellungExportierenButton;
  JButton searchArticle;
  private JLabel caption;
  private JLabel itemAmount;
  private IntegerParseField shopNumber;

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

  void focusOnAmount() {
    amount.requestFocusInWindow();
  }

  int getShopNumber() {
    return shopNumber.getSafeValue();
  }

  void setShopNumber(int s) {
    shopNumber.setText(String.valueOf(s));
  }

  void setNetPrice(double s) {
    netPrice.setText(String.format("%.2f€", s));
  }

  private static String getDueDateAsString(PreOrder preOrder) {
    boolean isSlow = preOrder.getArticle().getName().contains("*V*");
    String displayText = isSlow ? "ab " : "";
    if (preOrder.getDueDate().isAfter(LocalDate.now())) {
      displayText += Date.INSTANT_DATE.format(preOrder.getDueDate());
    } else {
      displayText =
          "NL "
              + displayText
              + Date.INSTANT_DATE.format(
                  LocalDate.now()
                      .with(
                          TemporalAdjusters.next(
                              Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class))));
    }
    return displayText;
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
            Columns.create("Benutzer", e -> e.getUser().getFullName(true)),
            new CustomizableColumn<PreOrder>("Ladennummer", PreOrder::getKBNumber)
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            new CustomizableColumn<PreOrder>(
                    "Kornkraftnummer", e -> e.getArticle().getSuppliersItemNumber())
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Produktname", e -> e.getArticle().getName()),
            new CustomizableColumn<PreOrder>(
                    "Netto-Preis",
                    e -> String.format("%.2f€", PreOrderModel.containerNetPrice(e.getArticle())))
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            new CustomizableColumn<>("Anzahl", PreOrder::getAmount)
                .withLeftClickConsumer(controller::editAmount)
                .withRightClickConsumer(controller::editAmount)
                .withHorizontalAlignment(SwingConstants.CENTER)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create(
                "Bestellt am",
                e -> e.getOrderedOn() == null ? "" : Date.INSTANT_DATE.format(e.getOrderedOn()),
                SwingConstants.RIGHT),
            Columns.create("erwartete Lieferung", PreOrderView::getDueDateAsString));
    if (!controller.restrictToLoggedIn)
      preOrders.addColumnAtIndex(
          0,
          Columns.createIconColumn(
              "ausgeliefert",
              e -> controller.isDelivered(e) ? selected : unselected,
              controller::toggleDelivery,
              e -> showSelectionPopup(),
              100));
    if (controller.userMayEdit()) {
      preOrders.addColumn(
          Columns.createIconColumn(
              IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED),
              controller::delete,
              e -> e.getOrderedOn() == null));
    }
    user = new AdvancedComboBox<>(e -> e.getFullName(true));
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
        "Es konnte kein Kornkraft-Artikel mit dieser Kornkraft-/"
            + Setting.STORE_NAME.getStringValue()
            + "-Nummer gefunden werden.");
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

    shopNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (controller.searchShopNo()) {
              if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                amount.selectAll();
                amount.requestFocusInWindow();
              }
            }
          }
        });

    user.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              kkNumber.requestFocusInWindow();
            }
          }
        });
    user.addActionListener(e -> userAction(false));
    add.addActionListener(e -> controller.add());

    preOrders.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
              controller.delete(getSelectedOrders());
            }
          }
        });

    amount.addActionListener(e -> controller.add());
    abhakplanButton.addActionListener(e -> controller.printChecklist());
    searchArticle.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 20, new Color(49, 114, 128)));
    bestellungExportierenButton.addActionListener(e -> controller.exportPreOrder());
    close.addActionListener(e -> back());
  }

  private void userAction(boolean fromFnKey) {
    if (!fromFnKey) {
      enableControls(true);
    }
    if (controller.searchKK()) {
      amount.requestFocusInWindow();
      if (fromFnKey) {
        controller.add();
      }
    } // kkNumber.requestFocusInWindow();
  }

  void fnKeyAction(String i) {
    setAmount(i);
    userAction(true);
  }

  void enableControls(boolean enabled) {
    searchArticle.setEnabled(enabled);
    kkNumber.setEnabled(enabled);
    shopNumber.setEnabled(enabled);
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

  public void setCaption(String forWho, boolean editable) {
    this.caption.setText(
        "<html><body><h2>Hier werden die Vorbestellungen für <em>"
            + forWho
            + "</em> angezeigt."
            + (editable ? " Die Bestellungen können hier auch bearbeitet und ergänzt werden." : "")
            + "</h2></body></html>");
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

  public void messageNothingToExport() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Es gibt keine Vorbestellungen, die noch nicht exportiert wurden!",
        "Vorbestellungsexport",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageExportCanceled() {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Der Export der Vorbestellung wurde abgebrochen oder ist fehlgeschlagen!",
        "Vorbestellungsexport",
        JOptionPane.WARNING_MESSAGE);
  }

  public void messageIsNotKKArticle(boolean ísShopOrder) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Zur Zeit können hier nur Kornkraft Artikel "
            + (ísShopOrder
                ? "bestellt werden."
                : "vorbestellt werden.\nFür andere Lieferanten bitte einen Bestellzettel ausfüllen."),
        "Falscher Lieferant",
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

  public boolean confirmDelivery(long numDelivered, long numOverdue) {
    if (numDelivered == 0 && numOverdue == 0) {
      return true;
    }
    Tools.beep();
    String message = "";
    if (numDelivered > 0) {
      message =
          numDelivered
              + " Vorbestellung"
              + (numDelivered == 1
                  ? " ist als ausgeliefert markiert und wird"
                  : "en sind als ausgeliefert markiert und werden")
              + " aus der Vorbestellung entfernt.";
    }
    if (numOverdue > 0) {
      if (!message.isEmpty()) {
        message += "\n";
      }
      message +=
          numOverdue
              + " überfällige Vorbestellung"
              + (numOverdue == 1
                  ? " bleibt in der Liste und wird"
                  : "en bleiben in der Liste und werden")
              + " hoffentlich bald nachgeliefert...";
    }
    return JOptionPane.showConfirmDialog(
            getContent(), message, "Vorbestellung schließen", JOptionPane.OK_CANCEL_OPTION)
        == JOptionPane.OK_OPTION;
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

  public LocalDate inputDeliveryDate() {
    JPanel datePickerPanel = new JPanel();
    datePickerPanel.setLayout(new BoxLayout(datePickerPanel, BoxLayout.Y_AXIS));
    JLabel infoText = new JLabel("Bitte das Lieferdatum auswählen:");
    DatePicker datePicker = new DatePicker();
    datePicker.setDate(
        LocalDate.now()
            .minusDays(2)
            .with(
                TemporalAdjusters.next(
                    Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class))));
    datePickerPanel.add(infoText);
    datePickerPanel.add(datePicker);

    if (JOptionPane.showConfirmDialog(
            getContent(), datePickerPanel, "Abhakplan", JOptionPane.OK_CANCEL_OPTION)
        == JOptionPane.CANCEL_OPTION) {
      return null;
    }
    return datePicker.getDate();
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

  public void setItemAmount(String s) {
    itemAmount.setText(s);
  }

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
    final JScrollPane scrollPane1 = new JScrollPane();
    main.add(
        scrollPane1,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    preOrders.setDoubleBuffered(false);
    preOrders.setDragEnabled(false);
    preOrders.setIntercellSpacing(new Dimension(3, 1));
    preOrders.setRowMargin(1);
    preOrders.setRowSelectionAllowed(true);
    preOrders.setShowHorizontalLines(true);
    scrollPane1.setViewportView(preOrders);
    insertSection = new JPanel();
    insertSection.setLayout(new GridBagLayout());
    main.add(
        insertSection,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    amount = new IntegerParseField();
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(amount, gbc);
    containerSize = new JLabel();
    containerSize.setText("Label");
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(containerSize, gbc);
    sellingPrice = new JLabel();
    sellingPrice.setText("Label");
    gbc = new GridBagConstraints();
    gbc.gridx = 9;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(sellingPrice, gbc);
    name = new JLabel();
    name.setBackground(new Color(-1));
    Font nameFont = this.$$$getFont$$$(null, Font.BOLD, -1, name.getFont());
    if (nameFont != null) name.setFont(nameFont);
    name.setText("Label");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(name, gbc);
    final JLabel label1 = new JLabel();
    label1.setText("Produktname");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label1, gbc);
    final JLabel label2 = new JLabel();
    label2.setText("Gebindegröße");
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label2, gbc);
    final JLabel label3 = new JLabel();
    label3.setText("Verkaufspreis");
    gbc = new GridBagConstraints();
    gbc.gridx = 9;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label3, gbc);
    add = new PermissionButton();
    add.setAlignmentY(0.0f);
    add.setHorizontalTextPosition(0);
    add.setText("Übernehmen");
    gbc = new GridBagConstraints();
    gbc.gridx = 10;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.insets = new Insets(0, 3, 0, 0);
    insertSection.add(add, gbc);
    final JLabel label4 = new JLabel();
    label4.setText("Nettopreis");
    gbc = new GridBagConstraints();
    gbc.gridx = 8;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label4, gbc);
    netPrice = new JLabel();
    netPrice.setText("Label");
    gbc = new GridBagConstraints();
    gbc.gridx = 8;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(netPrice, gbc);
    final JLabel label5 = new JLabel();
    label5.setText("Lieferantennr.");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label5, gbc);
    kkNumber = new IntegerParseField();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(kkNumber, gbc);
    final JLabel label6 = new JLabel();
    label6.setText("Benutzer");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label6, gbc);
    user.setAlignmentX(0.0f);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 3);
    insertSection.add(user, gbc);
    searchArticle = new JButton();
    searchArticle.setBorderPainted(false);
    searchArticle.setContentAreaFilled(false);
    searchArticle.setText("");
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(searchArticle, gbc);
    final JLabel label7 = new JLabel();
    label7.setText("Packungsmenge");
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    insertSection.add(label7, gbc);
    itemAmount = new JLabel();
    itemAmount.setText("Label");
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(itemAmount, gbc);
    final JLabel label8 = new JLabel();
    label8.setText("Anzahl");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label8, gbc);
    final JLabel label9 = new JLabel();
    label9.setText("KB-Nr.");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(label9, gbc);
    shopNumber = new IntegerParseField();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 3, 0, 3);
    insertSection.add(shopNumber, gbc);
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel1,
        new GridConstraints(
            3,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    panel1.add(
        spacer1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    close = new JButton();
    close.setText("Schließen");
    panel1.add(
        close,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    abhakplanButton = new JButton();
    abhakplanButton.setText("Abhakplan");
    panel1.add(
        abhakplanButton,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    bestellungExportierenButton = new JButton();
    bestellungExportierenButton.setText("Bestellung exportieren");
    panel1.add(
        bestellungExportierenButton,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    caption = new JLabel();
    caption.setText("{captionText}");
    main.add(
        caption,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
  }

  /** @noinspection ALL */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    Font font =
        new Font(
            resultName,
            style >= 0 ? style : currentFont.getStyle(),
            size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback =
        isMac
            ? new Font(font.getFamily(), font.getStyle(), font.getSize())
            : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource
        ? fontWithFallback
        : new FontUIResource(fontWithFallback);
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
