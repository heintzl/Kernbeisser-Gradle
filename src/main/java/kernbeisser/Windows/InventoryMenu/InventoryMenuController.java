package kernbeisser.Windows.InventoryMenu;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class InventoryMenuController implements IController<InventoryMenuView, InventoryMenuModel> {

  private final InventoryMenuModel model;
  private final InventoryMenuView view;

  InventoryMenuController(InventoryMenuView view) {
    this.view = view;
    model = new InventoryMenuModel();
  }

  @Override
  public @NotNull InventoryMenuModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
