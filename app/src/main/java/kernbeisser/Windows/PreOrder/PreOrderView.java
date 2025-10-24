package kernbeisser.Windows.PreOrder;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import jakarta.persistence.NoResultException;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
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
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Useful.Constants;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Icons;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PreOrderView implements IView<PreOrderController> {

  private PermissionButton submit;
  private ObjectTable<PreOrder> preOrders;
  private IntegerParseField amount;
  private JLabel name;
  private JLabel containerSize;
  private JPanel main;
  private JPanel insertSection;
  private JLabel netPrice;
  private AdvancedComboBox<User> user;
  private IntegerParseField kkNumber;
  private JButton close;
  @Getter private JButton abhakplanButton;
  @Getter private JButton bestellungExportierenButton;
  @Getter private JButton searchCatalog;
  private JLabel caption;
  private JCheckBox duplexPrint;
  private JButton defaultSortOrder;
  private JButton editPreOrder;
  private JButton deletePreOrder;
  private JButton cancelEdit;
  private JButton findByShopNumber;
  private JButton findAlternativeByShopNumber;
  private DatePicker latestWeekOfDelivery;
  private JTextArea comment;
  private IntegerParseField alternativeKkNumber;
  @Getter private JButton searchCatalogAlternative;
  private JLabel alternativeName;
  private JLabel alternativeContainerSize;
  private JLabel alternativeNetPrice;
  private JLabel currentWeekOfYear;
  private JButton clearAlternative;
  private JPopupMenu popupSelectionColumn;

  private Mode mode;
  private User addModeUser;

  @Linked private PreOrderController controller;

  void setInsertSectionEnabled(boolean b) {
    insertSection.setVisible(b);
  }

  void setItemName(String s) {
    name.setText(s);
  }

  void setAlternativeItemName(String s) {
    alternativeName.setText(s);
  }

  void setNetPrice(Double s) {
    if (s == null) {
      netPrice.setText("");
    } else {
      netPrice.setText(String.format("%.2f€", s));
    }
  }

  void setAlternativeNetPrice(Double s) {
    if (s == null) {
      alternativeNetPrice.setText("");
    } else {
      alternativeNetPrice.setText(String.format("%.2f€", s));
    }
  }

  void setContainerSize(String s) {
    containerSize.setText(s);
  }

  void setAlternativeContainerSize(String s) {
    alternativeContainerSize.setText(s);
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

  void setKkNumber(String s) {
    kkNumber.setText(s);
  }

  Optional<Integer> getAlternativeKkNumber() {
    try {
      return Optional.of(alternativeKkNumber.getSafeValue());
    } catch (RuntimeException e) {
      if (e.getCause() instanceof IncorrectInput) {
        return Optional.empty();
      }
      throw e;
    }
  }

  void setAlternativeKkNumber(String s) {
    alternativeKkNumber.setText(s);
  }

  Optional<Integer> getLatestWeekOfDelivery() {
    LocalDate date = latestWeekOfDelivery.getDate();
    if (date == null) {
      return Optional.empty();
    }
    return Optional.of(date.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
  }

  void setLatestWeekOfDelivery(PreOrder preOrder) {
    Integer weekOfYear = preOrder.getLatestWeekOfDelivery();
    if (weekOfYear == null) {
      latestWeekOfDelivery.setDate(null);
    } else {
      LocalDate date =
          LocalDate.now()
              .with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear)
              .with(WeekFields.ISO.dayOfWeek(), DayOfWeek.FRIDAY.getValue());
      int weekOfCreation =
          LocalDate.ofInstant(preOrder.getCreateDate(), Date.CURRENT_ZONE)
              .get(ChronoField.ALIGNED_WEEK_OF_YEAR);
      if (weekOfCreation >= weekOfYear) {
        date = date.plusYears(1);
      }
      latestWeekOfDelivery.setDate(date);
    }
  }

  String getComment() {
    return comment.getText();
  }

  void setComment(String s) {
    comment.setText(s);
  }

  void focusOnAmount() {
    amount.requestFocusInWindow();
  }

  public boolean getDuplexPrint() {
    return duplexPrint.isSelected();
  }

  private static String getDueDateAsString(PreOrder preOrder) {
    boolean isSlow = preOrder.getCatalogEntry().getBezeichnung().contains("*V*");
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

  private static final Icon selfIcon = Icons.defaultIcon(FontAwesome.USER, new Color(0x008515));
  private static final Icon cloudIcon = Icons.defaultIcon(FontAwesome.CLOUD, new Color(0x8C8C8C));
  private static final Icon posIcon =
      Icons.defaultIcon(FontAwesome.SHOPPING_BASKET, new Color(0xAC1200));
  private static final Icon shopManagerIcon =
      Icons.defaultIcon(FontAwesome.USER_O, new Color(0x0038CD));

  private static Icon getCreationTypeIcon(PreOrder p) {
    switch (p.getCreationType()) {
      case SELF -> {
        return selfIcon;
      }
      case ONLINE -> {
        return cloudIcon;
      }
      case POS -> {
        return posIcon;
      }
      default -> {
        return shopManagerIcon;
      }
    }
  }

  private void createUIComponents() {
    CustomizableColumn<PreOrder> hiddenSortColumn =
        Columns.<PreOrder>create("", p -> Date.INSTANT_DATE_TIME_SEC.format(p.getCreateDate()))
            .withSorter(Column.DATE_TIME_SORTER(Date.INSTANT_DATE_TIME_SEC))
            .withColumnAdjustor(
                e -> {
                  e.setMinWidth(0);
                  e.setMaxWidth(0);
                  e.setPreferredWidth(0);
                });
    preOrders =
        new ObjectTable<PreOrder>(
            Columns.createIconColumn("Herkunft", PreOrderView::getCreationTypeIcon)
                .withHorizontalAlignment(SwingConstants.CENTER)
                .withPreferredWidth(35)
                .withTooltip(
                    p -> Optional.ofNullable(p.getCreatedBy()).map(User::getFullName).orElse("")),
            Columns.<PreOrder>create("Besteller", e -> e.getUser().getFullName(true))
                .withColumnAdjustor(e -> e.setPreferredWidth(120)),
            Columns.<PreOrder>create("KK-Nummer", e -> e.getCatalogEntry().getArtikelNr())
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<PreOrder>create("Produktname", e -> e.getCatalogEntry().getBezeichnung())
                .withColumnAdjustor(e -> e.setPreferredWidth(200)),
            Columns.<PreOrder>create("Gebinde", e -> e.getCatalogEntry().getBestelleinheit())
                .withColumnAdjustor(e -> e.setPreferredWidth(50)),
            Columns.<PreOrder>create(
                    "Netto-Pr.",
                    e ->
                        String.format(
                            "%.2f€", PreOrderModel.containerNetPrice(e.getCatalogEntry())))
                .withHorizontalAlignment(SwingConstants.RIGHT)
                .withSorter(Column.NUMBER_SORTER),
            Columns.<PreOrder>create(
                    "Aktion bis",
                    e ->
                        e.getCatalogEntry().isOffer()
                            ? Date.INSTANT_DATE.format(
                                e.getCatalogEntry().getAktionspreisGueltigBis())
                            : "-")
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE))
                .withPreferredWidth(50)
                .withHorizontalAlignment(SwingConstants.RIGHT),
            Columns.<PreOrder>create("Anzahl", PreOrder::getAmount)
                .withLeftClickConsumer(controller::editAmount)
                .withRightClickConsumer(controller::editAmount)
                .withHorizontalAlignment(SwingConstants.CENTER)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(40),
            Columns.<PreOrder>create(
                    "Ersatz-Artikel",
                    p ->
                        Optional.ofNullable(p.getAlternativeCatalogEntry())
                            .map(CatalogEntry::getArtikelNr)
                            .orElse(""))
                .withSorter(Column.NUMBER_SORTER)
                .withTooltip(
                    p ->
                        Optional.ofNullable(p.getAlternativeCatalogEntry())
                            .map(CatalogEntry::getBezeichnung)
                            .orElse("")),
            Columns.create("bis KW", PreOrder::getLatestWeekOfDelivery)
                .withPreferredWidth(40)
                .withSorter(Column.NUMBER_SORTER)
                .withFgColor(p -> PreOrderModel.isOverdue(p) ? Color.RED : Color.BLACK),
            Columns.<PreOrder>create(
                    "Bemerkung", p -> Tools.ifNull(p.getComment(), "").replace("\n", " // "))
                .withPreferredWidth(150)
                .withTooltip(PreOrder::getComment)
                .withLeftClickConsumer(controller::editComment)
                .withRightClickConsumer(controller::editComment),
            Columns.<PreOrder>create(
                    "eingegeben am",
                    e -> Date.INSTANT_DATE.format(e.getCreateDate()),
                    SwingConstants.RIGHT)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)),
            Columns.<PreOrder>create(
                    "exportiert am",
                    e -> e.getOrderedOn() == null ? "" : Date.INSTANT_DATE.format(e.getOrderedOn()),
                    SwingConstants.RIGHT)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE)),
            Columns.create("erwartete Lieferung", PreOrderView::getDueDateAsString));
    if (controller.isPreOrderManager()) {
      Icon selected =
          IconFontSwing.buildIcon(
              FontAwesome.CHECK_SQUARE, Tools.scaleWithLabelScalingFactor(20), new Color(0x38FF00));
      Icon unselected =
          IconFontSwing.buildIcon(
              FontAwesome.SQUARE, Tools.scaleWithLabelScalingFactor(20), new Color(0xC7C7C7));
      JMenuItem popupSelectAll = new JMenuItem("alle auswählen");
      popupSelectAll.addActionListener(e -> setAllDelivered(true));
      JMenuItem popupDeselectAll = new JMenuItem("alle abwählen");
      popupDeselectAll.addActionListener(e -> setAllDelivered(false));
      popupSelectionColumn = new JPopupMenu();
      popupSelectionColumn.add(popupSelectAll);
      popupSelectionColumn.add(popupDeselectAll);
      preOrders.addColumnAtIndex(
          0,
          Columns.createIconColumn(
              "ausgeliefert",
              e -> controller.isDelivered(e) ? selected : unselected,
              controller::toggleDelivery,
              e -> showSelectionPopup(),
              70));
    }
    if (controller.isEditAllowed()) {
      preOrders.addColumn(
          Columns.createIconColumn(
              IconFontSwing.buildIcon(FontAwesome.TRASH, 20, Color.RED),
              controller::delete,
              e -> e.getOrderedOn() == null));
    }
    user = new AdvancedComboBox<>(e -> e.getFullName(true));
    preOrders.addColumnAtIndex(0, hiddenSortColumn);
    setDefaultSortOrder();
    latestWeekOfDelivery = new DatePicker(new DatePickerSettings(Locale.GERMANY));
    latestWeekOfDelivery.getSettings().setWeekNumbersDisplayed(true, false);
  }

  private void showSelectionPopup() {
    Point mousePosition = preOrders.getMousePosition();
    popupSelectionColumn.show(preOrders, mousePosition.x, mousePosition.y);
  }

  private void setDefaultSortOrder() {
    preOrders.setSortKeys(new RowSorter.SortKey(0, SortOrder.DESCENDING));
    preOrders.sort();
  }

  private void setAllDelivered(boolean allDelivered) {
    controller.setAllDelivered(allDelivered);
    popupSelectionColumn.setVisible(false);
  }

  public void setPreOrders(Collection<PreOrder> preOrders) {
    this.preOrders.setObjects(preOrders);
  }

  private Collection<PreOrder> getSelectedOrders() {
    return preOrders.getSelectedObjects();
  }

  public void noItemFound() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Es konnte kein Kornkraft-Artikel mit dieser Kornkraft-/"
            + Setting.STORE_NAME.getStringValue()
            + "-Nummer gefunden werden.");
  }

  public void resetArticleNr() {
    kkNumber.setText("");
    amount.setText("1");
    kkNumber.requestFocusInWindow();
    latestWeekOfDelivery.setDate(null);
    alternativeKkNumber.setText("");
    comment.setText("");
  }

  public void repaintTable() {
    preOrders.repaint();
  }

  private void clearItemDetails() {
    controller.setSelectedEntry(null);
    setItemName("");
    setContainerSize("");
    setNetPrice(null);
  }

  private void clearAlternativeItemDetails() {
    setAlternativeItemName("");
    setAlternativeContainerSize("");
    setAlternativeNetPrice(null);
  }

  public void pasteEntryDataInView(CatalogEntry entry, boolean targetAlternative) {
    if (entry == null) {
      if (targetAlternative) {
        clearAlternativeItemDetails();
      } else {
        clearItemDetails();
      }
      return;
    }
    String bezeichnung = entry.getBezeichnung();
    String bestellEinheit = entry.getBestelleinheit();
    String artikelNr = entry.getArtikelNr();
    Double containerNetPrice = PreOrderModel.containerNetPrice(entry);

    if (targetAlternative) {
      setAlternativeKkNumber(artikelNr);
      setAlternativeItemName(bezeichnung);
      setAlternativeContainerSize(bestellEinheit);
      setAlternativeNetPrice(containerNetPrice);
      return;
    }
    controller.setSelectedEntry(entry);
    setKkNumber(artikelNr);
    setItemName(bezeichnung);
    setContainerSize(bestellEinheit);
    setNetPrice(containerNetPrice);
    Optional.ofNullable(entry.getErsatzArtikelNr())
        .flatMap(nr -> controller.getEntryByKKNr(nr))
        .ifPresent(e -> pasteEntryDataInView(e, true));
  }

  @Override
  public void initialize(PreOrderController controller) {
    kkNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            Optional<CatalogEntry> searchResult = controller.searchKK(getKkNumber());
            pasteEntryDataInView(searchResult.orElse(null), false);
            if (e.getKeyCode() == KeyEvent.VK_ENTER && searchResult.isPresent()) {
              submitAction();
            }
          }
        });

    alternativeKkNumber.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyReleased(KeyEvent e) {
            Optional<CatalogEntry> searchResult =
                getAlternativeKkNumber().flatMap(controller::searchKK);
            pasteEntryDataInView(searchResult.orElse(null), true);
            if (e.getKeyCode() == KeyEvent.VK_ENTER && searchResult.isPresent()) {
              submitAction();
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
    Icon searchIcon = IconFontSwing.buildIcon(FontAwesome.SEARCH, 20, new Color(49, 114, 128));
    searchCatalog.setIcon(searchIcon);
    searchCatalog.setToolTipText("Katalog durchsuchen");
    searchCatalogAlternative.setIcon(searchIcon);
    searchCatalogAlternative.setToolTipText("Katalog nach Alternative durchsuchen");

    findByShopNumber.addActionListener(e -> controller.findArtikelNrByShopNumber(false));
    findByShopNumber.setToolTipText("Katalog-Eintrag über Ladennummer suchen");
    findByShopNumber.setIcon(Icons.SHOP_ICON);
    findAlternativeByShopNumber.addActionListener(e -> controller.findArtikelNrByShopNumber(true));
    findAlternativeByShopNumber.setToolTipText(
        "Alternativ-Katalog-Eintrag über Ladennummer suchen");
    findAlternativeByShopNumber.setIcon(Icons.SHOP_ICON);
    clearAlternative.setIcon(Icons.clearInputIcon);
    clearAlternative.addActionListener(
        e -> {
          setAlternativeKkNumber("");
          clearAlternativeItemDetails();
        });
    clearAlternative.setToolTipText("Alternativ-Katalog-Eintrag löschen");
    bestellungExportierenButton.addActionListener(e -> controller.exportPreOrder());
    close.addActionListener(e -> back());
    defaultSortOrder.addActionListener(e -> setDefaultSortOrder());
    editPreOrder.addActionListener(e -> startEditPreOrder());
    cancelEdit.addActionListener(e -> cancelEditPreOrder());
    deletePreOrder.addActionListener(e -> deletePreOrder());
    currentWeekOfYear.setText("KW: %d".formatted(Constants.CURRENT_WEEK_OF_YEAR));
    mode = Mode.ADD;
    refreshUIMode();
  }

  private void userAction(boolean fromFnKey) {
    if (!fromFnKey) {
      enableControls(true);
    }
    if (controller.searchKK(getKkNumber()).isPresent()) {
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
    searchCatalog.setEnabled(enabled);
    searchCatalogAlternative.setEnabled(enabled);
    findByShopNumber.setEnabled(enabled);
    findAlternativeByShopNumber.setEnabled(enabled);
    kkNumber.setEnabled(enabled);
    alternativeKkNumber.setEnabled(enabled);
    latestWeekOfDelivery.setEnabled(enabled);
    clearAlternative.setEnabled(enabled);
    comment.setEnabled(enabled);
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
    setAmount(Integer.toString(preOrder.getAmount()));
    pasteEntryDataInView(preOrder.getCatalogEntry(), false);
    pasteEntryDataInView(preOrder.getAlternativeCatalogEntry(), true);
    setComment(preOrder.getComment());
    setLatestWeekOfDelivery(preOrder);
    enableControls(true);
  }

  private void cancelEditPreOrder() {
    setMode(Mode.ADD);
  }

  private void deletePreOrder() {
    preOrders.getSelectedObject().ifPresent(p -> controller.forceDelete(p));
  }

  void submitAction() {
    switch (mode) {
      case ADD:
        controller.add();
        break;
      case EDIT:
        preOrders.getSelectedObject().ifPresent(p -> controller.edit(p));
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
    boolean isPreOrderManager = controller.isPreOrderManager();
    if (addMode) {
      user.getModel().setSelectedItem(addModeUser);
      enableControls(addModeUser != null);
      controller.noEntryFound();
      resetArticleNr();
    } else {
      addModeUser = (User) user.getSelectedItem();
    }
    bestellungExportierenButton.setEnabled(isPreOrderManager && addMode);
    abhakplanButton.setEnabled(isPreOrderManager && addMode);
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
    Optional<PreOrder> activeOrder = preOrders.getSelectedObject();
    editPreOrder.setEnabled(
        mode == Mode.ADD
            && activeOrder.isPresent()
            && (controller.isPreOrderManager() || activeOrder.get().getOrderedOn() == null)
            && controller.isEditAllowed());
  }

  public User getUser() {
    return (User) user.getSelectedItem();
  }

  public void addPreOrder(PreOrder order) {
    preOrders.add(order);
    setDefaultSortOrder();
  }

  public void refreshPreOrder(PreOrder preOrder, PreOrder newPreOrder) {
    preOrders.replace(preOrder, newPreOrder);
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
    enableControls(!controller.isPreOrderManager());
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
    if (!controller.isPreOrderManager() || (numDelivered == 0 && numOverdue == 0)) {
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
    switch (controller.getPreOrderCreator()) {
      case SELF -> {
        return "Meine Vorbestellung";
      }
      case POS -> {
        return "%ss Vorbestellung"
            .formatted(controller.getRestrictToUser().map(User::getFullName).orElse("?"));
      }
    }
    return "Vorbestellung";
  }

  public String inputShopNumber(boolean inputError) {
    return JOptionPane.showInputDialog(
        getContent(),
        (inputError ? "Fehlerhafte Eingabe!\n" : "") + "Laden-Artikelnummer:",
        "Suche nach Laden-Artikelnummer",
        JOptionPane.QUESTION_MESSAGE);
  }

  public void messageArticleNotInCatalog(int shopNumber) {
    JOptionPane.showMessageDialog(
        getContent(),
        "Zur Ladennummer "
            + shopNumber
            + " existiert kein gültiger Katalogeintrag!\n"
            + "Entweder ist die Nummer falsch, oder der Artikel steht\n"
            + "nicht (mehr) im Kornkraft-Katalog.",
        "Es wurde kein Katalog-Artikel gefunden",
        JOptionPane.WARNING_MESSAGE);
  }

  // @spotless:off

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        main.add(scrollPane1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        preOrders.setDoubleBuffered(false);
        preOrders.setDragEnabled(false);
        preOrders.setIntercellSpacing(new Dimension(3, 1));
        preOrders.setRowMargin(1);
        preOrders.setRowSelectionAllowed(true);
        preOrders.setShowHorizontalLines(true);
        scrollPane1.setViewportView(preOrders);
        insertSection = new JPanel();
        insertSection.setLayout(new GridLayoutManager(4, 11, new Insets(0, 0, 0, 0), -1, -1));
        main.add(insertSection, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        amount = new IntegerParseField();
        insertSection.add(amount, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(25, -1), null, 0, false));
        containerSize = new JLabel();
        containerSize.setText("");
        insertSection.add(containerSize, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Produktname");
        insertSection.add(label1, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Gebinde");
        insertSection.add(label2, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Nettopreis");
        insertSection.add(label3, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        netPrice = new JLabel();
        netPrice.setText("");
        insertSection.add(netPrice, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Lieferantennr.");
        insertSection.add(label4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        kkNumber = new IntegerParseField();
        insertSection.add(kkNumber, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Benutzer");
        insertSection.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(150, -1), new Dimension(150, 16), new Dimension(150, -1), 0, false));
        user.setAlignmentX(0.0f);
        insertSection.add(user, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(350, -1), new Dimension(-1, 30), new Dimension(350, -1), 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Anzahl");
        insertSection.add(label6, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        findByShopNumber = new JButton();
        findByShopNumber.setBorderPainted(false);
        findByShopNumber.setContentAreaFilled(false);
        findByShopNumber.setOpaque(false);
        findByShopNumber.setText("");
        insertSection.add(findByShopNumber, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        name = new JLabel();
        name.setBackground(new Color(-1));
        Font nameFont = this.$$$getFont$$$(null, Font.BOLD, -1, name.getFont());
        if (nameFont != null) name.setFont(nameFont);
        name.setText("");
        insertSection.add(name, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        alternativeKkNumber = new IntegerParseField();
        insertSection.add(alternativeKkNumber, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        findAlternativeByShopNumber = new JButton();
        findAlternativeByShopNumber.setBorderPainted(false);
        findAlternativeByShopNumber.setContentAreaFilled(false);
        findAlternativeByShopNumber.setOpaque(false);
        findAlternativeByShopNumber.setText("");
        insertSection.add(findAlternativeByShopNumber, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Ersatzartikel falls nicht verfügbar");
        insertSection.add(label7, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        insertSection.add(latestWeekOfDelivery, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Lieferung bis (Kalenderwoche)");
        insertSection.add(label8, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comment = new JTextArea();
        comment.setMargin(new Insets(1, 1, 1, 1));
        insertSection.add(comment, new GridConstraints(1, 8, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 16), null, 0, false));
        cancelEdit = new JButton();
        cancelEdit.setBorderPainted(true);
        cancelEdit.setText("Abbrechen");
        insertSection.add(cancelEdit, new GridConstraints(3, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        submit = new PermissionButton();
        submit.setAlignmentY(0.0f);
        submit.setHorizontalTextPosition(0);
        submit.setText("Übernehmen");
        insertSection.add(submit, new GridConstraints(3, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deletePreOrder = new JButton();
        deletePreOrder.setText("Löschen");
        insertSection.add(deletePreOrder, new GridConstraints(1, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        searchCatalog = new JButton();
        searchCatalog.setBorderPainted(false);
        searchCatalog.setContentAreaFilled(false);
        searchCatalog.setText("");
        insertSection.add(searchCatalog, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        searchCatalogAlternative = new JButton();
        searchCatalogAlternative.setBorderPainted(false);
        searchCatalogAlternative.setContentAreaFilled(false);
        searchCatalogAlternative.setText("");
        insertSection.add(searchCatalogAlternative, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, -1), null, 0, false));
        alternativeName = new JLabel();
        alternativeName.setBackground(new Color(-1));
        Font alternativeNameFont = this.$$$getFont$$$(null, Font.BOLD, -1, alternativeName.getFont());
        if (alternativeNameFont != null) alternativeName.setFont(alternativeNameFont);
        alternativeName.setText("");
        insertSection.add(alternativeName, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Kommentar zur Bestellung");
        insertSection.add(label9, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        alternativeContainerSize = new JLabel();
        alternativeContainerSize.setText("");
        insertSection.add(alternativeContainerSize, new GridConstraints(3, 6, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        alternativeNetPrice = new JLabel();
        alternativeNetPrice.setText("");
        insertSection.add(alternativeNetPrice, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearAlternative = new JButton();
        clearAlternative.setText("");
        insertSection.add(clearAlternative, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(15, 15), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        main.add(panel1, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        close = new JButton();
        close.setText("Schließen");
        panel1.add(close, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        abhakplanButton = new JButton();
        abhakplanButton.setText("Abhakplan");
        panel1.add(abhakplanButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bestellungExportierenButton = new JButton();
        bestellungExportierenButton.setText("Bestellung exportieren");
        panel1.add(bestellungExportierenButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        duplexPrint = new JCheckBox();
        duplexPrint.setText("doppelseitig Drucken");
        panel1.add(duplexPrint, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        defaultSortOrder = new JButton();
        defaultSortOrder.setText("nach Eingabereihenfolge sortieren");
        panel1.add(defaultSortOrder, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editPreOrder = new JButton();
        editPreOrder.setAlignmentY(0.0f);
        editPreOrder.setHorizontalTextPosition(0);
        editPreOrder.setText("Bestellung bearbeiten");
        panel1.add(editPreOrder, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        caption = new JLabel();
        caption.setText("{captionText}");
        main.add(caption, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentWeekOfYear = new JLabel();
        currentWeekOfYear.setText("Aktuelle KW");
        main.add(currentWeekOfYear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
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
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

    // @spotless:on
}
