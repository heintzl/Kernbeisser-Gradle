package kernbeisser.Windows.PreOrder;

import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.persistence.NoResultException;
import javax.swing.*;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class PreOrderController extends Controller<PreOrderView, PreOrderModel> {

  private final KeyCapture keyCapture;
  private final BarcodeCapture barcodeCapture;
  boolean restrictToLoggedIn;

  public PreOrderController(boolean restrictToLoggedIn) {
    super(new PreOrderModel());
    this.restrictToLoggedIn = restrictToLoggedIn;
    keyCapture = new KeyCapture();
    barcodeCapture = new BarcodeCapture(this::processBarcode);
  }

  @Override
  public @NotNull PreOrderModel getModel() {
    return model;
  }

  boolean searchKK() {
    PreOrderView view = getView();
    if (view.getKkNumber() != 0) {
      Optional<Article> searchResult = model.getItemByKkNumber(view.getKkNumber());
      if (searchResult.isPresent()) {
        pasteDataInView(searchResult.get());
        return true;
      } else {
        noArticleFound();
        return false;
      }
    } else {
      noArticleFound();
      return false;
    }
  }

  void add() {
    var view = getView();
    try {
      PreOrder order = obtainFromView();
      if (!order.getArticle().getSupplier().equals(Supplier.getKKSupplier())) {
        view.messageIsNotKKArticle();
        return;
      }
      if (order.getUser() == null) {
        view.notifyNoUserSelected();
        return;
      }
      model.add(order);
      view.addPreOrder(order);
      noArticleFound();
      view.resetArticleNr();
    } catch (NoResultException e) {
      view.noItemFound();
    }
  }

  void delete(PreOrder preOrder) {
    if (preOrder == null) {
      getView().noPreOrderSelected();
      return;
    }
    delete(Collections.singleton(preOrder));
  }

  void delete(Collection<PreOrder> preOrders) {
    if (preOrders.size() == 0) {
      getView().noPreOrderSelected();
      return;
    }
    for (PreOrder preOrder : preOrders) {
      if (model.remove(preOrder)) {
        getView().remove(preOrder);
      }
    }
  }

  void noArticleFound() {
    pasteDataInView(Articles.getEmptyArticle());
  }

  @Override
  protected boolean commitClose() {
    Collection<PreOrder> delivery = model.getDelivery();
    int numDelivered = delivery.size();
    Collection<PreOrder> remaining = model.getAllPreOrders(false);
    remaining.removeAll(delivery);
    long numOverdue =
        remaining.stream().filter(p -> p.getDueDate().isBefore(LocalDate.now())).count();
    if (!getView().confirmDelivery(numDelivered, numOverdue)) {
      return false;
    }
    model.close();
    return true;
  }

  void pasteDataInView(Article articleKornkraft) {
    var view = getView();
    double containerSize = articleKornkraft.getContainerSize();
    view.setContainerSize(new DecimalFormat("0.###").format(containerSize));
    view.setNetPrice(PreOrderModel.containerNetPrice(articleKornkraft));
    view.setSellingPrice(
        String.format(
            "%.2f€",
            ShoppingItem.displayOnlyShoppingItem(articleKornkraft, 0, true).getRetailPrice()
                * containerSize));
    view.setItemName(articleKornkraft.getName());
    view.setItemAmount(Articles.getContentAmount(articleKornkraft));
  }

  private PreOrder obtainFromView() {
    PreOrder preOrder = new PreOrder();
    Article article = findArticle();
    var view = getView();
    preOrder.setUser(view.getUser());
    preOrder.setArticle(article);
    preOrder.setAmount(view.getAmount());
    preOrder.setInfo(article.getInfo());
    return preOrder;
  }

  private Article findArticle() {
    if (getView().getKkNumber() == 0) throw new NoResultException();
    return model.getItemByKkNumber(getView().getKkNumber()).orElseThrow(NoResultException::new);
  }

  void insert(Article article) {
    if (article == null) throw new NullPointerException("cannot insert null as PreOrder");
    var view = getView();
    if (view.getUser() == null) {
      getView().notifyNoUserSelected();
      return;
    }
    PreOrder preOrder = new PreOrder();
    preOrder.setUser(view.getUser());
    preOrder.setArticle(article);
    preOrder.setAmount(view.getAmount());
    preOrder.setInfo(article.getInfo());

    model.add(preOrder);
    getView().addPreOrder(preOrder);
  }

  void articleSearch(Article a) {
    pasteDataInView(a);
    getView().setKkNumber(a.getSuppliersItemNumber());
  }

  void openSearchWindow() {
    var searchWindow = new ArticleSelectorController(this::articleSearch);
    searchWindow.modifyNamedComponent(
        "KKFilter",
        c -> {
          var checkbox = (JCheckBox) c;
          if (!checkbox.isSelected()) {
            checkbox.doClick();
          }
          c.setEnabled(false);
        });
    searchWindow.openIn(new SubWindow(getView().traceViewContainer()));
  }

  @Override
  public void fillView(PreOrderView view) {
    keyCapture.add(KeyEvent.VK_F2, () -> view.fnKeyAction("2"));
    keyCapture.add(KeyEvent.VK_F3, () -> view.fnKeyAction("3"));
    keyCapture.add(KeyEvent.VK_F4, () -> view.fnKeyAction("4"));
    keyCapture.add(KeyEvent.VK_F5, () -> view.fnKeyAction("5"));
    keyCapture.add(KeyEvent.VK_F6, () -> view.fnKeyAction("6"));
    keyCapture.add(KeyEvent.VK_F7, () -> view.fnKeyAction("8"));
    keyCapture.add(KeyEvent.VK_F8, () -> view.fnKeyAction("10"));
    keyCapture.addALT(KeyEvent.VK_S, this::openSearchWindow);
    keyCapture.addCTRL(KeyEvent.VK_F, this::openSearchWindow);
    boolean editable = userMayEdit();
    view.setInsertSectionEnabled(editable);
    String preOrdersFor =
        restrictToLoggedIn
            ? LogInModel.getLoggedIn().getFullName()
            : "den Laden und alle Mitglieder";
    view.setCaption(preOrdersFor, editable);
    view.enableControls(false);
    if (restrictToLoggedIn) {
      view.setUsers(Collections.singletonList(LogInModel.getLoggedIn()));
      view.setUserEnabled(false);
    } else {
      view.setUsers(User.getAllUserFullNames(true, true));
    }
    view.setPreOrders(model.getAllPreOrders(restrictToLoggedIn));
    view.setAmount("1");
    view.searchArticle.addActionListener(e -> openSearchWindow());
    view.bestellungExportierenButton.setEnabled(!restrictToLoggedIn);
    view.abhakplanButton.setEnabled(!restrictToLoggedIn);
    noArticleFound();
  }

  boolean userMayEdit() {
    try {
      checkUserOrderContainerPermission();
      return true;
    } catch (PermissionKeyRequiredException e) {
      if (restrictToLoggedIn) {
        return Tools.canInvoke(this::checkOrderOwnContainerPermission);
      }
      return false;
    }
  }

  @Key(PermissionKey.ACTION_ORDER_OWN_CONTAINER)
  private void checkOrderOwnContainerPermission() {}

  @Key(PermissionKey.ACTION_ORDER_CONTAINER)
  private void checkUserOrderContainerPermission() {}

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return barcodeCapture.processKeyEvent(e) || keyCapture.processKeyEvent(e);
  }

  private void processBarcode(String s) {
    try {
      insert(model.getByBarcode(s));
    } catch (NoResultException e) {
      getView().noArticleFoundForBarcode(s);
    }
  }

  public void printChecklist() {
    LocalDate deliveryDate = getView().inputDeliveryDate();
    if (deliveryDate != null) {
      model.printCheckList(deliveryDate);
      getView().repaintTable();
    }
  }

  public void exportPreOrder() {
    PreOrderView view = getView();
    if (model.getUnorderedPreOrders().size() == 0) {
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
    String response = getView().inputAmount(preOrder.getAmount(), false);
    boolean exit = false;
    do {
      if (response == null || response.equals("")) {
        return;
      } else {
        try {
          int alteredAmount = Integer.parseInt(response);
          if (alteredAmount > 0) {
            model.setAmount(preOrder, alteredAmount);
            getView().refreshPreOrder(preOrder);
            exit = true;
          } else {
            throw (new NumberFormatException());
          }
        } catch (NumberFormatException exception) {
          response = getView().inputAmount(preOrder.getAmount(), true);
        }
      }
    } while (!exit);
  }
}
