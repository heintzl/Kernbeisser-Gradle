package kernbeisser.Windows.ManagePriceLists;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class ManagePriceListsController
    extends Controller<ManagePriceListsView, ManagePriceListsModel> implements ActionListener {

  public ManagePriceListsController() {
    super(new ManagePriceListsModel());
  }

  @Override
  public @NotNull ManagePriceListsModel getModel() {
    return model;
  }

  public Node<PriceList> getNode() {
    return PriceList.getPriceListsAsNode();
  }

  private void move() {
    if (getView().getSelectedNode() == null) getView().selectionRequired();
    else getView().requiresPriceList(this::move);
  }

  private void move(Node<PriceList> target) {
    PriceList selected = getView().getSelectedNode().getValue();
    if (target.getValue().getId() == selected.getId()) {
      getView().cannotMoveIntoSelf();
      return;
    }
    if (getView().commitMovement(selected, target.getValue())) {
      model.setSuperPriceList(selected, target.getValue());
      getView().requestRepaint();
    }
  }

  private void rename() {
    String name = getView().requestName();
    try {
      model.renamePriceList(getView().getSelectedNode().getValue(), name);
      getView().requestRepaint();
    } catch (PersistenceException e) {
      getView().nameAlreadyExists(name);
    }
  }

  private void add() {
    if (getView().getSelectedNode() == null) {
      getView().selectionRequired();
      return;
    }
    String name = getView().requestName();
    try {
      model.add(getView().getSelectedNode(), name);
      getView().requestRepaint();
    } catch (PersistenceException e) {
      getView().nameAlreadyExists(name);
    }
  }

  private void remove() {
    try {
      model.deletePriceList(getView().getSelectedNode().getValue());
      getView().requestRepaint();
    } catch (PersistenceException e) {
      getView().cannotDelete();
    }
  }

  private void moveItems(Node<PriceList> target) {
    PriceList selection = getView().getSelectedNode().getValue();
    if (getView().commitItemMovement(selection, target.getValue())) {
      model.moveItems(selection, target.getValue());
      getView().requestRepaint();
    }
  }

  private void moveItems() {
    if (getView().getSelectedNode() == null) {
      getView().selectionRequired();

    } else getView().requiresPriceList(this::moveItems);
  }

  private void print() {
    ManagePriceListsView view = getView();
    if (view.getSelectedNode() == null) {
      view.selectionRequired();
    } else {
      PriceList selectedList = view.getSelectedNode().getValue();
      List<Article> articles = getAllArticles(selectedList);
      if (articles.size() == 0) {
        view.selectionRequired();
      } else {
        model.print(selectedList);
      }
    }
  }

  List<Article> getAllArticles(PriceList priceList) {
    return priceList.getAllArticles();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand().toUpperCase()) {
      case "ADD":
        add();
        break;
      case "RENAME":
        rename();
        break;
      case "MOVE":
        move();
        break;
      case "DELETE":
        remove();
        break;
      case "MOVE_ITEMS":
        moveItems();
        break;
      case "PRINT":
        print();
        break;
      default:
        throw new UnsupportedOperationException(e.getActionCommand() + " is not a valid command");
    }
  }

  @Override
  public void fillView(ManagePriceListsView managePriceListsView) {}

  @Override
  @StaticAccessPoint
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[] {PermissionKey.ACTION_OPEN_MANAGE_PRICE_LISTS};
  }
}
