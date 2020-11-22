package kernbeisser.Windows.PreOrder;

import java.awt.event.KeyEvent;
import javax.persistence.NoResultException;
import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class PreOrderController extends Controller<PreOrderView, PreOrderModel> {

  public PreOrderController() {
    super(new PreOrderModel());
  }

  @Override
  public @NotNull PreOrderModel getModel() {
    return model;
  }

  void searchKK() {
    try {
      pasteDataInView(model.getItemByKkNumber(getView().getKkNumber()));
    } catch (NoResultException e) {
      noArticleFound();
    }
  }

  void add() {
    try {
      PreOrder order = obtainFromView();
      noArticleFound();
      getView().setKkNumber(0);
      model.add(order);
      getView().addPreOrder(order);
    } catch (NoResultException e) {
      getView().noItemFound();
    }
  }

  void delete() {
    PreOrder selected = getView().getSelectedOrder();
    if (selected == null) {
      getView().noPreOrderSelected();
      return;
    }
    model.remove(selected);
    getView().remove(selected);
  }

  void noArticleFound() {
    ArticleKornkraft empty = new ArticleKornkraft();
    empty.setName("Kein Artikel gefunden");
    empty.setSurchargeGroup(new SurchargeGroup());
    pasteDataInView(empty);
  }

  @Override
  protected boolean commitClose() {
    model.close();
    return true;
  }

  void pasteDataInView(ArticleKornkraft articleKornkraft) {
    var view = getView();
    view.setAmount(String.valueOf(1));
    view.setContainerSize(articleKornkraft.getContainerSize() + "");
    view.setNetPrice(articleKornkraft.getNetPrice());
    view.setSellingPrice(
        String.format("%.2f", new ShoppingItem(articleKornkraft, 0, false).getRetailPrice()));
    view.setItemName(articleKornkraft.getName());
  }

  private PreOrder obtainFromView() {
    PreOrder preOrder = new PreOrder();
    ArticleKornkraft article = findArticle();
    var view = getView();
    preOrder.setUser(view.getUser());
    preOrder.setItem(article);
    preOrder.setAmount(view.getAmount());
    preOrder.setInfo(article.getInfo());
    return preOrder;
  }

  private ArticleKornkraft findArticle() {
    if (getView().getKkNumber() == 0) throw new NoResultException();
    return model.getItemByKkNumber(getView().getKkNumber());
  }

  void insert(ArticleKornkraft articleBase) {
    if (articleBase == null) throw new NullPointerException("cannot insert null as PreOrder");
    PreOrder preOrder = new PreOrder();
    var view = getView();
    preOrder.setUser(view.getUser());
    preOrder.setItem(articleBase);
    preOrder.setAmount(view.getAmount());
    preOrder.setInfo(articleBase.getInfo());
    model.add(preOrder);
    getView().addPreOrder(preOrder);
  }

  @Override
  public void fillView(PreOrderView preOrderView) {
    preOrderView.setInsertSectionEnabled(PermissionKey.ACTION_ORDER_CONTAINER.userHas());
    preOrderView.setUser(model.getAllUser());
    preOrderView.setPreOrders(model.getAllPreOrders());
    noArticleFound();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  @Override
  protected boolean processKeyboardInput(KeyEvent e) {
    return new BarcodeCapture(this::processBarcode).processKeyEvent(e);
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
