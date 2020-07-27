package kernbeisser.Windows.InventoryMenu;

import javax.swing.*;
import kernbeisser.Windows.View;
import org.jetbrains.annotations.NotNull;

public class InventoryMenuView implements View<InventoryMenuController> {

  private final InventoryMenuController controller;

  public InventoryMenuView(InventoryMenuController controller) {
    this.controller = controller;
  }

  @Override
  public void initialize(InventoryMenuController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return null;
  }
}
