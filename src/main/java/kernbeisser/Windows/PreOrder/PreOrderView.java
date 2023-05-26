package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import javax.persistence.NoResultException;
import javax.swing.*;
import javax.swing.table.TableColumn;
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

  private PermissionButton submit;
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
                submitAction();
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
                submitAction();
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
    submit.addActionListener(e -> submitAction());
    preOrders.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
              controller.delete(getSelectedOrders());
            }
          }
        });
    preOrders.addSelectionListener(e -> enableEditPreorder());
    preOrders.addFocusListener(
        new FocusListener() {
          @Override
          public void focusGained(FocusEvent e) {}

          @Override
          public void focusLost(FocusEvent e) {
            enableEditPreorder();
          }
        });

    amount.addActionListener(e -> submitAction());
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
    submit.setEnabled(enabled);
  }

  private void startEditPreOrder() {
    try {
      PreOrder editableOrder = preOrders.getSelectedObject().orElseThrow(NoResultException::new);
      controller.startEditPreOrder(editableOrder);
      enableEditPreorder();
    } catch (NoResultException e) {
      noPreOrderSelected();
    }
  }

  void populatePreOrderEditor(PreOrder preOrder) {
    setMode(Mode.EDIT);
    user.getModel().setSelectedItem(preOrder.getUser());
    user.repaint();
    setShopNumber(preOrder.getArticle().getKbNumber());
    setAmount(Integer.toString(preOrder.getAmount()));
    controller.pasteDataInView(preOrder.getArticle(), true);
    enableControls(true);
  }

  private void cancelEditPreOrder() {
    setMode(Mode.ADD);
  }

  private void deletePreOrder() {
    controller.forceDelete(preOrders.getSelectedObject().get());
  }

  void submitAction() {
    switch (mode) {
      case ADD:
        controller.add();
        break;
      case EDIT:
        controller.edit(preOrders.getSelectedObject().get());
        user.setSelectedItem(null);
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
    enableEditPreorder();
    duplexPrint.setEnabled(addMode);
    defaultSortOrder.setEnabled(addMode);
    preOrders.setEnabled(addMode);
    deletePreOrder.setEnabled(!addMode);
    deletePreOrder.setVisible(!addMode);
    cancelEdit.setEnabled(!addMode);
    cancelEdit.setVisible(!addMode);
    submit.setText(addMode ? "Hinzufügen" : "Übernehmen");
  }

  private void enableEditPreorder() {
    editPreOrder.setEnabled(
        !controller.isRestrictToLoggedIn() && mode == Mode.ADD && preOrders.getSelectedRow() > -1);
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
            + "\nBitte wähle zuerst einen Benutzer aus,"
            + "\nauf dessen Namen die Vorbestellung ausgeführt werden soll.",
        "Kein Benutzer ausgewählt",
        JOptionPane.WARNING_MESSAGE);
  }

  public boolean confirmDelivery(long numDelivered, long numOverdue) {
    if (controller.isRestrictToLoggedIn() || (numDelivered == 0 && numOverdue == 0)) {
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

  void warningEditDelivered() {
    JOptionPane.showMessageDialog(
        getContent(),
        "Diese Vorbestellung ist als ausgeliefert gekennzeichnet.\n"
            + "Sie kann nicht mehr bearbeitet werden!",
        "Vorbestellung bearbeiten",
        JOptionPane.WARNING_MESSAGE);
  }

  boolean confirmEditOrdered() {
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
}
