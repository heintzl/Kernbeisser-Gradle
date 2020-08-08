package kernbeisser.Windows.InventoryMenu;

import javax.swing.*;

import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class InventoryMenuView implements View<InventoryMenuController> {

  @Linked
  private InventoryMenuController controller;

  @Override
  public void initialize(InventoryMenuController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return null;
  }
}
