package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.EditItems.EditItemsController;
import kernbeisser.Windows.EditSurchargeTables.EditSurchargeTables;
import kernbeisser.Windows.EditUsers.EditUsers;
import kernbeisser.Windows.ManagePriceLists.ManagePriceListsController;
import kernbeisser.Windows.Trasaction.TransactionController;
import org.jetbrains.annotations.NotNull;

public class CashierMenuController implements Controller<CashierMenuView, CashierMenuModel> {
  private final CashierMenuModel model;
  private final CashierMenuView view;

  public CashierMenuController(User user) {
    this.view = new CashierMenuView(this);
    model = new CashierMenuModel(user);
  }

  @Override
  public @NotNull CashierMenuView getView() {
    return view;
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
    new TransactionController(model.getUser()).openTab("Überweissungen tätigen");
  }
}
