package kernbeisser.Windows.InventoryMenu;

import kernbeisser.Windows.MVC.Controller;

public class InventoryMenuController extends Controller<InventoryMenuView, InventoryMenuModel> {

  InventoryMenuController(InventoryMenuView view) {
    super(new InventoryMenuModel());
  }

  @Override
  public void fillView(InventoryMenuView inventoryMenuView) {}
}
