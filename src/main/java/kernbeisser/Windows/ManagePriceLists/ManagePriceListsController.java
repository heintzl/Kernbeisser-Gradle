package kernbeisser.Windows.ManagePriceLists;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTree.Node;
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

  public void back() {
    view.back();
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

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "ADD":
        break;
      case "RENAME":
        break;
      case "EDIT":
        break;
      case "REMOVE":
        break;
    }
  }
}
