package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.EditItems.EditItemsController;
import kernbeisser.Windows.EditSurchargeTables.EditSurchargeTables;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.MVC.IController;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.Trasaction.TransactionController;
import org.jetbrains.annotations.NotNull;

public class CashierMenuController implements IController<CashierMenuView, CashierMenuModel> {
  private final CashierMenuModel model;
  private CashierMenuView view;

  public CashierMenuController(User owner) {
    model = new CashierMenuModel(owner);
  }

  @Override
  public @NotNull CashierMenuModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void openManageItems() {
    new EditItemsController().openTab("Artikel bearbeiten");
  }

  public void openManageSurchargeTables() {
    new EditSurchargeTables().openTab("Aufschlagstabellen bearbeiten");
  }

  public void openManageUsers() {
    new EditUsers().openTab("Nutzer bearbeiten");
  }

  public void openManagePriceLists() {
    new ManagePriceListsController().openTab("Preislisten bearbeiten");
  }

  public void openCashierMask() {
    new CashierShoppingMaskController().openTab("Ladendienst Einkaufsmaske");
  }

  public void openTransfer() {
    new TransactionController(model.getOwner()).openTab("Überweissungen tätigen");
  }
}
