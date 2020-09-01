package kernbeisser.Windows.ManagePriceLists;

import javax.persistence.PersistenceException;
import javax.swing.*;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class ManagePriceListsController
    implements IController<ManagePriceListsView, ManagePriceListsModel> {
  private final ManagePriceListsModel model;
  private final ManagePriceListsView view;

  // TODO When the root is selected, the displayCurrentSuperpriceList should be activated, too.
  // TODO Nice to have: At Changes in the Tree, not to reload the hole tree to keep expansion state
  // und selected Node

  public ManagePriceListsController() {
    this.view = new ManagePriceListsView(this);
    model = new ManagePriceListsModel();
  }

  void displayCurrentSuperPriceList() {
    view.setSuperPriceListName(view.getSelectedPriceList().getName());
  }

  void saveAction() {
    String priceListName = view.getPriceListName();
    if (priceListName.equals("")) {
      JOptionPane.showMessageDialog(
          view.getTopComponent(), "Bitte w\u00e4hlen sie einen korrekten Namen");
      return;
    }
    model.savePriceList(priceListName, view.getSelectedPriceList());
    refresh();
    return;
  }

  void renameAction() {
    PriceList toRename = view.getSelectedPriceList();
    String newName = view.getPriceListName();
    if (toRename == null) {
      return;
    }
    if (newName.equals("")) {
      JOptionPane.showMessageDialog(
          view.getTopComponent(), "Bitte w\u00e4hlen sie einen korrekten Namen");
      return;
    }
    model.renamePriceList(toRename, newName);
    refresh();
  }

  void deleteAction() {
    PriceList toDelete = view.getSelectedPriceList();
    if (toDelete == null) {
      return;
    }
    if (JOptionPane.showConfirmDialog(
            view.getTopComponent(),
            "Soll die Preisliste " + toDelete.getName() + " wirklich gel\u00f6scht werden")
        == 0) {
      try {
        model.deletePriceList(toDelete);
        refresh();
      } catch (PersistenceException e) {
        JOptionPane.showMessageDialog(
            view.getTopComponent(),
            "Preisliste konnte nicht gelöscht werden.\n Entweder hat diese Preisliste noch Unterpreislisten oder Artikel, die auf ihr stehen.");
      }
    }
  }

  public void refresh() {
    model.refresh();
    view.getPriceListTree().setModel(model.getPriceListTreeModel());
    view.setSuperPriceListName("");
    view.setPriceListName("");
  }

  // Only to override
  public void finish() {}

  public void back() {
    view.back();
  }

  @Override
  public @NotNull ManagePriceListsModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    refresh();
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
