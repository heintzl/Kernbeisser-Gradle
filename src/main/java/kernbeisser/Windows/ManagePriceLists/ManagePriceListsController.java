package kernbeisser.Windows.ManagePriceLists;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.persistence.PersistenceException;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class ManagePriceListsController
    implements IController<ManagePriceListsView, ManagePriceListsModel>, ActionListener {
  private final ManagePriceListsModel model;
  private ManagePriceListsView view;

  public ManagePriceListsController() {
    model = new ManagePriceListsModel();
  }

  @Override
  public @NotNull ManagePriceListsModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public Node<PriceList> getNode() {
    return PriceList.getPriceListsAsNode();
  }

  private void move() {
    if (view.getSelectedNode() == null) view.selectionRequired();
    else view.requiresPriceList(this::move);
  }

  private void move(Node<PriceList> target) {
    PriceList selected = view.getSelectedNode().getValue();
    if (target.getValue().getId() == selected.getId()) {
      view.cannotMoveIntoSelf();
      return;
    }
    if (view.commitMovement(selected, target.getValue())) {
      model.setSuperPriceList(selected, target.getValue());
      view.requestRepaint();
    }
  }

  private void rename() {
    String name = view.requestName();
    try {
      model.renamePriceList(view.getSelectedNode().getValue(), name);
      view.requestRepaint();
    } catch (PersistenceException e) {
      view.nameAlreadyExists(name);
    }
  }

  private void add() {
    if (view.getSelectedNode() == null) {
      view.selectionRequired();
      return;
    }
    String name = view.requestName();
    try {
      model.add(view.getSelectedNode(), name);
      view.requestRepaint();
    } catch (PersistenceException e) {
      view.nameAlreadyExists(name);
    }
  }

  private void remove() {
    try {
      model.deletePriceList(view.getSelectedNode().getValue());
      view.requestRepaint();
    } catch (PersistenceException e) {
      view.cannotDelete();
    }
  }

  private void moveItems(Node<PriceList> target) {
    PriceList selection = view.getSelectedNode().getValue();
    if (view.commitItemMovement(selection, target.getValue())) {
      model.moveItems(selection, target.getValue());
      view.requestRepaint();
    }
  }

  private void moveItems() {
    if (view.getSelectedNode() == null) {
      view.selectionRequired();

    } else view.requiresPriceList(this::moveItems);
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
      default:
        throw new UnsupportedOperationException(e.getActionCommand() + " is not a valid command");
    }
  }
}
