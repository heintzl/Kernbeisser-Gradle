package kernbeisser.Windows.PreOrder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Collection;
import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.CustomComponents.KeyCapture;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ShoppingMask.ArticleSelector.ArticleSelectorController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class PreOrderController extends Controller<PreOrderView, PreOrderModel> {

  KeyCapture keyCapture;

  public PreOrderController() {
    super(new PreOrderModel());
    keyCapture = new KeyCapture();
    PreOrderView view = getView();
    keyCapture.add(KeyEvent.VK_F2, () -> view.fnKeyAction("2"));
    keyCapture.add(KeyEvent.VK_F3, () -> view.fnKeyAction("3"));
    keyCapture.add(KeyEvent.VK_F4, () -> view.fnKeyAction("4"));
    keyCapture.add(KeyEvent.VK_F5, () -> view.fnKeyAction("5"));
    keyCapture.add(KeyEvent.VK_F6, () -> view.fnKeyAction("6"));
    keyCapture.add(KeyEvent.VK_F7, () -> view.fnKeyAction("8"));
    keyCapture.add(KeyEvent.VK_F8, () -> view.fnKeyAction("10"));
  }

  @Override
  public @NotNull PreOrderModel getModel() {
    return model;
  }

  boolean searchKK() {
    PreOrderView view = getView();
    if (view.getKkNumber() != 0) {
      try {
        pasteDataInView(model.getItemByKkNumber(view.getKkNumber()));
        return true;
      } catch (NoResultException e) {
        noArticleFound();
        return false;
      }
    } else {
      noArticleFound();
      return false;
    }
  }

  void add() {
    try {
      PreOrder order = obtainFromView();
      noArticleFound();
      getView().resetArticleNr();
      model.add(order);
      getView().addPreOrder(order);
    } catch (NoResultException e) {
      getView().noItemFound();
    }
  }

  void delete(PreOrder preOrder) {
    if (preOrder == null) {
      getView().noPreOrderSelected();
      return;
    }
    model.remove(preOrder);
    getView().remove(preOrder);
  }

  void delete(Collection<PreOrder> preOrders) {
    if (preOrders.size() == 0) {
      getView().noPreOrderSelected();
      return;
    }
    for (PreOrder preOrder : preOrders) {
      model.remove(preOrder);
      getView().remove(preOrder);
    }
  }

  void noArticleFound() {
    Article empty = new Article();
    empty.setName("Kein Artikel gefunden");
    empty.setSurchargeGroup(new SurchargeGroup());
    pasteDataInView(empty);
  }

  @Override
  protected boolean commitClose() {
    model.close();
    return true;
  }

  void pasteDataInView(Article articleKornkraft) {
    var view = getView();
    // view.setAmount(String.valueOf(1));
    double containerSize = articleKornkraft.getContainerSize();
    view.setContainerSize(new DecimalFormat("0.###").format(containerSize));
    view.setNetPrice(model.containerNetPrice(articleKornkraft));
    view.setSellingPrice(
        String.format(
            "%.2f€", new ShoppingItem(articleKornkraft, 0, true).getRetailPrice() * containerSize));
    view.setItemName(articleKornkraft.getName());
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
    return model.getItemByKkNumber(getView().getKkNumber());
  }

  void insert(Article article) {
    if (article == null) throw new NullPointerException("cannot insert null as PreOrder");
    PreOrder preOrder = new PreOrder();
    var view = getView();
    preOrder.setUser(view.getUser());
    preOrder.setArticle(article);
    preOrder.setAmount(view.getAmount());
    preOrder.setInfo(article.getInfo());
    model.add(preOrder);
    getView().addPreOrder(preOrder);
  }

  void openSearchWindow() {
    new ArticleSelectorController(this::pasteDataInView)
        .withCloseEvent(() -> getView().searchArticle.setEnabled(true))
        .openIn(new SubWindow(getView().traceViewContainer()));
    getView().searchArticle.setEnabled(false);
  }

  @Override
  public void fillView(PreOrderView preOrderView) {
    preOrderView.setInsertSectionEnabled(PermissionKey.ACTION_ORDER_CONTAINER.userHas());
    preOrderView.setUser(User.getAllUserFullNames(true));
    preOrderView.setPreOrders(model.getAllPreOrders());
    preOrderView.enableControls(false);
    preOrderView.setAmount("1");
    preOrderView.searchArticle.addActionListener(e -> openSearchWindow());

    noArticleFound();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return (new BarcodeCapture(this::processBarcode).processKeyEvent(e)
        || keyCapture.processKeyEvent(e));
  }

  private void processBarcode(String s) {
    try {
      insert(model.getByBarcode(s));
    } catch (NoResultException e) {
      getView().noArticleFoundForBarcode(s);
    }
  }

  public void printChecklist() {
    model.printCheckList();
  }
}
