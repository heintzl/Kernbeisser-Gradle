package kernbeisser.Windows.PreOrder;

import com.github.lgooddatepicker.components.DatePicker;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.Locale;
import javax.persistence.NoResultException;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableColumn;
import javax.swing.text.StyleContext;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.PermissionButton;
import kernbeisser.CustomComponents.TextFields.IntegerParseField;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Setter;
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
  private IntegerParseField shopNumber;

  private JCheckBox duplexPrint;
  private JButton defaultSortOrder;
  private JButton editPreOrder;
  private JButton deletePreOrder;
  private JButton cancelEdit;
  private JPopupMenu popupSelectionColumn;

  @Setter private Mode mode;

  private User addModeUser;

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
    if (s == 0) {
      shopNumber.setText("");
    } else {
      shopNumber.setText(String.valueOf(s));
    }
  }

  void setNetPrice(double s) {
    netPrice.setText(String.format("%.2f€", s));
  }

  public boolean getDuplexPrint() {
    return duplexPrint.isSelected();
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
    if (!controller.isRestrictToLoggedIn()) {
      JMenuItem popupSelectAll = new JMenuItem("alle auswählen");
      popupSelectAll.addActionListener(e -> setAllDelivered(true));
      JMenuItem popupDeselectAll = new JMenuItem("alle abwählen");
      popupDeselectAll.addActionListener(e -> setAllDelivered(false));
      popupSelectionColumn = new JPopupMenu();
      popupSelectionColumn.add(popupSelectAll);
      popupSelectionColumn.add(popupDeselectAll);
    }
    preOrders =
        new ObjectTable<>(
            Columns.<PreOrder>create("Benutzer", e -> e.getUser().getFullName(true))
                .withColumnAdjustor(e -> e.setPreferredWidth(150)),
            Columns.<PreOrder>create("Ladennummer", PreOrder::getKBNumber)
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<PreOrder>create(
                    "Kornkraftnummer", e -> e.getArticle().getSuppliersItemNumber())
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<PreOrder>create("Produktname", e -> e.getArticle().getName())
                .withColumnAdjustor(e -> e.setPreferredWidth(350)),
            Columns.<PreOrder>create(
                    "Netto-Preis",
                    e -> String.format("%.2f€", PreOrderModel.containerNetPrice(e.getArticle())))
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<PreOrder>create("Anzahl", PreOrder::getAmount)
                .withLeftClickConsumer(controller::editAmount)
                .withRightClickConsumer(controller::editAmount)
                .withHorizontalAlignment(SwingConstants.CENTER)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create(
                "eingegeben am",
                e -> Date.INSTANT_DATE.format(e.getCreateDate()),
                SwingConstants.RIGHT),
            Columns.create(
                "exportiert am",
                e -> e.getOrderedOn() == null ? "" : Date.INSTANT_DATE.format(e.getOrderedOn()),
                SwingConstants.RIGHT),
            Columns.create("erwartete Lieferung", PreOrderView::getDueDateAsString));
    Column<PreOrder> sortColumn = Columns.create("Id", PreOrder::getId);
    if (!controller.isRestrictToLoggedIn())
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
    preOrders.addColumnAtIndex(0, sortColumn);
    TableColumn hiddenColumn = preOrders.getColumnModel().getColumn(0);
    hiddenColumn.setMinWidth(0);
    hiddenColumn.setMaxWidth(0);
    setDefaultSortOrder();
    user = new AdvancedComboBox<>(e -> e.getFullName(true));
  }

  private void showSelectionPopup() {
    Point mousePosition = preOrders.getMousePosition();
    popupSelectionColumn.show(preOrders, mousePosition.x, mousePosition.y);
  }

  void setDefaultSortOrder() {
    preOrders.setSortKeys(new RowSorter.SortKey(0, SortOrder.DESCENDING));
    preOrders.sort();
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
                addAction();
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
                addAction();
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
    add.addActionListener(e -> addAction());
    preOrders.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
              controller.delete(getSelectedOrders());
            }
          }
        });
    preOrders.addSelectionListener(e -> editPreOrder.setEnabled(true));
    preOrders.addFocusListener(
        new FocusListener() {
          @Override
          public void focusGained(FocusEvent e) {}

          @Override
          public void focusLost(FocusEvent e) {
            editPreOrder.setEnabled(preOrders.getSelectedRow() > -1);
          }
        });

    amount.addActionListener(e -> controller.add());
    abhakplanButton.addActionListener(e -> controller.printChecklist());
    searchArticle.setIcon(IconFontSwing.buildIcon(FontAwesome.SEARCH, 20, new Color(49, 114, 128)));
    bestellungExportierenButton.addActionListener(e -> controller.exportPreOrder());
    close.addActionListener(e -> back());
    defaultSortOrder.addActionListener(e -> setDefaultSortOrder());
    editPreOrder.addActionListener(e -> startEditPreOrder());
    cancelEdit.addActionListener(e -> cancelEditPreOrder());
    deletePreOrder.addActionListener(e -> deletePreOrder());
    mode = Mode.ADD;
    refreshUIMode();
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
    }
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

  private void startEditPreOrder() {
    try {
      PreOrder editableOrder = preOrders.getSelectedObject().orElseThrow(NoResultException::new);
      if (controller.isDelivered(editableOrder)) {
        warningEditDelivered();
        return;
      }
      if (editableOrder.getOrderedOn() != null && !confirmEditOrdered()) {
        return;
      }
      setMode(Mode.EDIT);
      user.getModel().setSelectedItem(editableOrder.getUser());
      setShopNumber(editableOrder.getArticle().getKbNumber());
      controller.pasteDataInView(editableOrder.getArticle(), true);
      enableControls(true);
    } catch (NoResultException e) {
      noPreOrderSelected();
    }
  }

  private void cancelEditPreOrder() {
    setMode(Mode.ADD);
  }

  private void deletePreOrder() {
    controller.forceDelete(preOrders.getSelectedObject().get());
  }

  void addAction() {
    switch (mode) {
      case ADD:
        controller.add();
        break;
      case EDIT:
        controller.edit(preOrders.getSelectedObject().get());
        break;
      default:
    }
  }

  public void setMode(Mode mode) {
    this.mode = mode;
    refreshUIMode();
  }

  void refreshUIMode() {
    boolean addMode = mode == Mode.ADD;
    boolean restrictToLoggedIn = controller.isRestrictToLoggedIn();
    user.setEnabled(addMode);
    if (addMode) {
      user.getModel().setSelectedItem(addModeUser);
      enableControls(addModeUser != null);
      controller.noArticleFound(false);
      resetArticleNr();
    } else {
      addModeUser = (User) user.getSelectedItem();
    }
    bestellungExportierenButton.setEnabled(!restrictToLoggedIn && addMode);
    abhakplanButton.setEnabled(!restrictToLoggedIn && addMode);
    close.setEnabled(addMode);
    editPreOrder.setEnabled(!restrictToLoggedIn && addMode && preOrders.getSelectedRow() > -1);
    duplexPrint.setEnabled(addMode);
    defaultSortOrder.setEnabled(addMode);
    preOrders.setEnabled(addMode);
    deletePreOrder.setEnabled(!addMode);
    deletePreOrder.setVisible(!addMode);
    cancelEdit.setEnabled(!addMode);
    cancelEdit.setVisible(!addMode);
    add.setText(addMode ? "Hinzufügen" : "Übernehmen");
  }

  public User getUser() {
    return (User) user.getSelectedItem();
  }

  public void addPreOrder(PreOrder order) {
    preOrders.add(order);
    setDefaultSortOrder();
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
    enableControls(controller.isRestrictToLoggedIn());
  }

  public void setCaption(String forWho, boolean editable) {
    this.caption.setText(
        "<html><body><h2>Hier werden die Vorbestellungen für <em>"
            + forWho
            + "</em> angezeigt."
            + (editable ? " Die Bestellungen können hier auch bearbeitet und ergänzt werden." : "")
            + "</h2></body></html>");
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

  public LocalDate inputDeliveryDate() {
    JPanel datePickerPanel = new JPanel();
    datePickerPanel.setLayout(new BoxLayout(datePickerPanel, BoxLayout.Y_AXIS));
    JLabel infoText = new JLabel("Bitte das Lieferdatum auswählen:");
    DatePicker datePicker = new DatePicker();
    datePicker.setDate(
        LocalDate.now()
            .minusDays(2)
            .with(
                TemporalAdjusters.next(Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class)))
            .plus(1, ChronoUnit.DAYS));
    datePickerPanel.add(infoText);
    datePickerPanel.add(datePicker);

    if (JOptionPane.showConfirmDialog(
            getContent(), datePickerPanel, "Abhakplan", JOptionPane.OK_CANCEL_OPTION)
        == JOptionPane.CANCEL_OPTION) {
      return null;
    }
    return datePicker.getDate();
  }

  private void warningEditDelivered() {
    JOptionPane.showMessageDialog(
        getContent(),
        "Diese Vorbestellung ist als ausgeliefert gekennzeichnet.\n"
            + "Sie kann nicht mehr bearbeitet werden!",
        "Vorbestellung bearbeiten",
        JOptionPane.WARNING_MESSAGE);
  }

  private boolean confirmEditOrdered() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Achtung, diese Vorbestellung ist bereits für Kornkraft exportiert worden.\n"
                + "Soll sie jetzt wirklich noch bearbeitet werden?",
            "Vorbestellung bearbeiten",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE)
        == JOptionPane.YES_OPTION;
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
    return (controller.isRestrictToLoggedIn() ? "Meine " : "") + "Vorbestellung";
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
    insertSection.setLayout(new GridLayoutManager(2, 12, new Insets(0, 0, 0, 0), -1, -1));
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
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    amount = new IntegerParseField();
    insertSection.add(
        amount,
        new GridConstraints(
            1,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    containerSize = new JLabel();
    containerSize.setText("Label");
    insertSection.add(
        containerSize,
        new GridConstraints(
            1,
            6,
            1,
            1,
            GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    sellingPrice = new JLabel();
    sellingPrice.setText("Label");
    insertSection.add(
        sellingPrice,
        new GridConstraints(
            1,
            8,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    name = new JLabel();
    name.setBackground(new Color(-1));
    Font nameFont = this.$$$getFont$$$(null, Font.BOLD, -1, name.getFont());
    if (nameFont != null) name.setFont(nameFont);
    name.setText("Label");
    insertSection.add(
        name,
        new GridConstraints(
            1,
            5,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            new Dimension(300, -1),
            new Dimension(300, -1),
            new Dimension(300, -1),
            0,
            false));
    final JLabel label1 = new JLabel();
    label1.setText("Produktname");
    insertSection.add(
        label1,
        new GridConstraints(
            0,
            5,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label2 = new JLabel();
    label2.setText("Gebinde");
    insertSection.add(
        label2,
        new GridConstraints(
            0,
            6,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label3 = new JLabel();
    label3.setText("Verkaufspreis");
    insertSection.add(
        label3,
        new GridConstraints(
            0,
            8,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    add = new PermissionButton();
    add.setAlignmentY(0.0f);
    add.setHorizontalTextPosition(0);
    add.setText("Übernehmen");
    insertSection.add(
        add,
        new GridConstraints(
            1,
            11,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label4 = new JLabel();
    label4.setText("Nettopreis");
    insertSection.add(
        label4,
        new GridConstraints(
            0,
            7,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    netPrice = new JLabel();
    netPrice.setText("Label");
    insertSection.add(
        netPrice,
        new GridConstraints(
            1,
            7,
            1,
            1,
            GridConstraints.ANCHOR_EAST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label5 = new JLabel();
    label5.setText("Lieferantennr.");
    insertSection.add(
        label5,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    kkNumber = new IntegerParseField();
    insertSection.add(
        kkNumber,
        new GridConstraints(
            1,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label6 = new JLabel();
    label6.setText("Benutzer");
    insertSection.add(
        label6,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            new Dimension(150, -1),
            new Dimension(150, 16),
            new Dimension(150, -1),
            0,
            false));
    user.setAlignmentX(0.0f);
    insertSection.add(
        user,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            new Dimension(200, -1),
            new Dimension(200, 30),
            new Dimension(200, -1),
            1,
            false));
    searchArticle = new JButton();
    searchArticle.setBorderPainted(false);
    searchArticle.setContentAreaFilled(false);
    searchArticle.setText("");
    insertSection.add(
        searchArticle,
        new GridConstraints(
            1,
            4,
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
    final JLabel label7 = new JLabel();
    label7.setText("Anzahl");
    insertSection.add(
        label7,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label8 = new JLabel();
    label8.setText("KB-Nr.");
    insertSection.add(
        label8,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    shopNumber = new IntegerParseField();
    insertSection.add(
        shopNumber,
        new GridConstraints(
            1,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    deletePreOrder = new JButton();
    deletePreOrder.setText("Löschen");
    insertSection.add(
        deletePreOrder,
        new GridConstraints(
            1,
            10,
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
    cancelEdit = new JButton();
    cancelEdit.setText("Abbrechen");
    insertSection.add(
        cancelEdit,
        new GridConstraints(
            1,
            9,
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
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
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
            2,
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
            6,
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
            5,
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
            4,
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
    duplexPrint = new JCheckBox();
    duplexPrint.setText("doppelseitig Drucken");
    panel1.add(
        duplexPrint,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    defaultSortOrder = new JButton();
    defaultSortOrder.setText("nach Eingabereihenfolge sortieren");
    panel1.add(
        defaultSortOrder,
        new GridConstraints(
            0,
            0,
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
    editPreOrder = new JButton();
    editPreOrder.setAlignmentY(0.0f);
    editPreOrder.setHorizontalTextPosition(0);
    editPreOrder.setText("Bestellung bearbeiten");
    panel1.add(
        editPreOrder,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_NONE,
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
