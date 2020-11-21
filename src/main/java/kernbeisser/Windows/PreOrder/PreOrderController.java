package kernbeisser.Windows.PreOrder;

import javax.persistence.NoResultException;
import kernbeisser.DBEntities.ArticleKornkraft;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class PreOrderController extends Controller<PreOrderView, PreOrderModel> {

  public PreOrderController(User user) {
    super(new PreOrderModel());
  }

  @Override
  public @NotNull PreOrderModel getModel() {
    return model;
  }

  void searchKK() {
    try {
      getView().setKbNumber(0);
      pasteDataInView(model.getItemByKkNumber(getView().getKkNumber()));
    } catch (NoResultException e) {
      noArticleFound();
    }
  }

  void add() {
    try {
      PreOrder order = obtainFromView();
      noArticleFound();
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
    int kkNumber = getView().getKkNumber();
    int kbNumber = getView().getKbNumber();
    try {
      if (kbNumber == 0) throw new NoResultException();
      return model.getItemByKbNumber(kbNumber);
    } catch (NoResultException e) {
      if (kkNumber == 0) throw new NoResultException();
      return model.getItemByKkNumber(kkNumber);
    }
  }

  void searchKB() {
    try {
      getView().setKkNumber(0);
      pasteDataInView(model.getItemByKbNumber(getView().getKbNumber()));
    } catch (NoResultException e) {
      noArticleFound();
    }
  }

  @Override
  public void fillView(PreOrderView preOrderView) {
    preOrderView.setInsertSectionEnabled(PermissionKey.ACTION_ORDER_CONTAINER.userHas());
    noArticleFound();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
