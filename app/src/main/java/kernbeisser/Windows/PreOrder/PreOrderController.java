package kernbeisser.Windows.PreOrder;

import jakarta.persistence.NoResultException;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.swing.*;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.Dialogs.DateSelectorDialog;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PreOrderCreator;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.InvalidValue;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.PreOrder.CatalogSelector.CatalogSelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.groump.*;

public class PreOrderController extends Controller<PreOrderView, PreOrderModel> {

  private final KeyCapture keyCapture;
  private final BarcodeCapture barcodeCapture;
  @Getter private final PreOrderCreator preOrderCreator;
  private CatalogEntry selectedEntry = null;
  @Getter private final Optional<User> restrictToUser;
  @Getter private final boolean isPreOrderManager;

  public PreOrderController(@NotNull PreOrderCreator preOrderCreator, @Nullable User orderingUser) {
    super(new PreOrderModel());
    this.restrictToUser = Optional.ofNullable(orderingUser);
    this.preOrderCreator = preOrderCreator;
    keyCapture = new KeyCapture();
    barcodeCapture = new BarcodeCapture(this::processBarcode);
    isPreOrderManager =
        preOrderCreator == PreOrderCreator.PRE_ORDER_MANAGER
            && Tools.canInvoke(model::checkGeneralOrderPlacementPermission);
  }

  @Override
  public @NotNull PreOrderModel getModel() {
    return model;
  }

  public boolean searchKK() {
    PreOrderView view = getView();
    if (view.getKkNumber() != 0) {
      Optional<CatalogEntry> searchResult = model.getEntryByKkNumber(view.getKkNumber());
      if (searchResult.isPresent()) {
        pasteDataInView(searchResult.get());
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  void add() {
    PreOrderView view = getView();
    try {
      PreOrder order = obtainFromView();
      order.setCreationType(preOrderCreator);
      model.add(order);
      view.addPreOrder(order);
      noEntryFound();
      view.resetArticleNr();
    } catch (NoResultException e) {
      view.noItemFound();
    } catch (InvalidValue ignored) {
    }
  }

  void edit(PreOrder preOrder) {
    PreOrderView view = getView();
    try {
      PreOrder order = obtainFromView();
      view.refreshPreOrder(preOrder, model.edit(preOrder, order));
      view.setMode(Mode.ADD);
    } catch (NoResultException e) {
      view.noItemFound();
    } catch (InvalidValue ignored) {
    }
  }

  void delete(PreOrder preOrder) {
    PreOrderView view = getView();
    if (preOrder == null) {
      view.noPreOrderSelected();
      return;
    }
    delete(Collections.singleton(preOrder));
  }

  void delete(Collection<PreOrder> preOrders) {
    if (preOrders.isEmpty()) {
      getView().noPreOrderSelected();
      return;
    }
    for (PreOrder preOrder : preOrders) {
      if (model.remove(preOrder, false)) {
        getView().remove(preOrder);
      }
    }
  }

  void forceDelete(PreOrder preOrder) {
    PreOrderView view = getView();
    if (preOrder == null) {
      view.noPreOrderSelected();
      return;
    }
    if (model.remove(preOrder, true)) {
      getView().remove(preOrder);
    }
    view.setMode(Mode.ADD);
  }

  void noEntryFound() {
    pasteDataInView(new CatalogEntry());
    selectedEntry = null;
  }

  @Override
  protected boolean commitClose() {
    Collection<PreOrder> delivery = model.getDelivery();
    int numDelivered = delivery.size();
    Collection<PreOrder> remaining = model.getAllPreOrders();
    remaining.removeAll(delivery);
    long numOverdue =
        remaining.stream()
            .filter(p -> Tools.ifNull(p.getDueDate(), LocalDate.MAX).isBefore(LocalDate.now()))
            .count();
    if (!getView().confirmDelivery(numDelivered, numOverdue)) {
      return false;
    }
    model.close();
    return true;
  }

  void pasteDataInView(CatalogEntry entry) {
    selectedEntry = entry;
    PreOrderView view = getView();
    view.setContainerSize(entry.getBestelleinheit());
    view.setItemName(entry.getBezeichnung());
    view.setKkNumber(entry.getArtikelNr());
    try {
      view.setNetPrice(PreOrderModel.containerNetPrice(entry));
    } catch (NullPointerException e) {
      view.setNetPrice(0.00);
    }
  }

  private PreOrder obtainFromView() throws InvalidValue, NoResultException {
    if (selectedEntry == null) {
      throw new NoResultException();
    }
    PreOrder preOrder = new PreOrder();
    PreOrderView view = getView();
    preOrder.setUser(view.getUser());
    preOrder.setCatalogEntry(selectedEntry);
    preOrder.setAmount(view.getAmount());
    preOrder.setInfo(selectedEntry.getInfo());
    if (preOrder.getUser() == null) {
      view.notifyNoUserSelected();
      throw new InvalidValue();
    }
    return preOrder;
  }

  void insert(CatalogEntry entry) {
    if (entry == null) throw new NullPointerException("cannot insert null as PreOrder");
    PreOrderView view = getView();
    if (view.getUser() == null) {
      getView().notifyNoUserSelected();
      return;
    }
    PreOrder preOrder = new PreOrder();
    preOrder.setUser(view.getUser());
    preOrder.setCatalogEntry(entry);
    preOrder.setAmount(view.getAmount());
    preOrder.setInfo(entry.getInfo());
    preOrder.setCreationType(preOrderCreator);
    model.add(preOrder);
    getView().addPreOrder(preOrder);
  }

  void catalogSearch(CatalogEntry entry) {
    pasteDataInView(entry);
    getView().setKkNumber(entry.getArtikelNr());
    getView().focusOnAmount();
  }

  void openSearchWindow() {
    CatalogSelectorController searchWindow = new CatalogSelectorController(this::catalogSearch);
    searchWindow.modifyNamedComponent(
        "KKFilter",
        c -> {
          JCheckBox checkbox = (JCheckBox) c;
          if (!checkbox.isSelected()) {
            checkbox.doClick();
          }
          c.setEnabled(false);
        });
    searchWindow.openIn(new SubWindow(getView().traceViewContainer()));
  }

  @Override
  public void fillView(PreOrderView view) {
    keyCapture.addF2ToF8NumberActions(view::fnKeyAction);
    keyCapture.addALT(KeyEvent.VK_S, this::openSearchWindow);
    keyCapture.addCTRL(KeyEvent.VK_F, this::openSearchWindow);
    boolean editable = userMayEdit();
    view.setInsertSectionEnabled(editable);
    String preOrdersFor;
    view.enableControls(false);
    if (restrictToUser.isPresent()) {
      User user = restrictToUser.get();
      view.setUsers(Collections.singletonList(user));
      view.setUserEnabled(false);
      view.setPreOrders(model.getPreOrdersByUser(user));
      preOrdersFor = LogInModel.getLoggedIn().getFullName();
    } else {
      view.setUsers(User.getAllUserFullNames(true, true));
      view.setPreOrders(model.getAllPreOrders());
      preOrdersFor = "den Laden und alle Mitglieder";
    }
    view.setCaption(preOrdersFor, editable);
    view.setAmount("1");
    view.searchCatalog.addActionListener(e -> openSearchWindow());
    view.bestellungExportierenButton.setEnabled(isPreOrderManager);
    view.abhakplanButton.setEnabled(isPreOrderManager);
    noEntryFound();
  }

  boolean userMayEdit() {
    try {
      model.checkUserOrderContainerPermission();
      return true;
    } catch (AccessDeniedException e) {
      if (preOrderCreator == PreOrderCreator.SELF) {
        return Tools.canInvoke(model::checkOrderOwnContainerPermission);
      }
      return false;
    }
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return barcodeCapture.processKeyEvent(e) || keyCapture.processKeyEvent(e);
  }

  private void processBarcode(String s) {
    try {
      insert(model.getByBarcode(s));
    } catch (NoResultException e) {
      Tools.noArticleFoundForBarcodeWarning(getView().getContent(), s);
    }
  }

  public void findArtikelNrByShopNumber() {
    PreOrderView view = getView();
    boolean inputError = false;
    boolean canceled = false;
    int shopNumber = 0;
    do {
      String input = view.inputShopNumber(inputError);
      try {
        if (input.isEmpty()) {
          canceled = true;
        }
        shopNumber = Integer.parseInt(input);
        inputError = false;
      } catch (NumberFormatException e) {
        inputError = true;
      }
    } while (inputError && !canceled);
    if (canceled) {
      return;
    }
    Optional<CatalogEntry> optEntry = model.findEntriesByShopNumber(shopNumber);
    if (optEntry.isPresent()) {
      CatalogEntry entry = optEntry.get();
      pasteDataInView(entry);
      view.setKkNumber(entry.getArtikelNr());
      view.focusOnAmount();
    } else {
      view.messageArticleNotInCatalog(shopNumber);
      findArtikelNrByShopNumber();
    }
  }

  public void printChecklist() {
    PreOrderView view = getView();
    LocalDate defaultDate =
        LocalDate.now()
            .minusDays(2)
            .with(
                TemporalAdjusters.next(Setting.KK_SUPPLY_DAY_OF_WEEK.getEnumValue(DayOfWeek.class)))
            .plus(1, ChronoUnit.DAYS);
    LocalDate deliveryDate =
        DateSelectorDialog.getDate(
            view.getContent(), "Abhakplan", "Bitte das Lieferdatum auswählen:", defaultDate);
    if (deliveryDate != null) {
      model.printCheckList(deliveryDate, view.getDuplexPrint());
      view.repaintTable();
    }
  }

  public void exportPreOrder() {
    PreOrderView view = getView();
    if (model.getUnorderedPreOrders().isEmpty()) {
      getView().messageNothingToExport();
      return;
    }
    if (model.exportPreOrder(view.getContent())) {
      view.messageExportSuccess();
    } else {
      view.messageExportCanceled();
    }
    getView().repaintTable();
  }

  void toggleDelivery(PreOrder p) {
    model.toggleDelivery(p);
    getView().repaintTable();
  }

  boolean isDelivered(PreOrder p) {
    return model.isDelivered(p);
  }

  void setAllDelivered(boolean allDelivered) {
    model.setAllDelivered(allDelivered);
    getView().repaintTable();
  }

  public void editAmount(PreOrder preOrder) {
    if (preOrder.getOrderedOn() != null) {
      return;
    }
    Integer newValue = Tools.integerInputDialog(getView().getContent(), preOrder.getAmount());
    if (newValue == null) {
      return;
    }
    model.setAmount(preOrder, newValue);
    getView().refreshPreOrder(preOrder, preOrder);
  }

  public void startEditPreOrder(PreOrder preOrder) {
    PreOrderView view = getView();
    if (isDelivered(preOrder)) {
      view.warningEditDelivered();
      return;
    }
    if (preOrder.getOrderedOn() != null && !view.confirmEditOrdered()) {
      return;
    }
    view.populatePreOrderEditor(preOrder);
  }
}
