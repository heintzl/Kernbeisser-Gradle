package kernbeisser.Windows.InventoryMenu;

import javax.swing.*;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class InventoryMenuView implements IView<InventoryMenuController> {

  @Linked private InventoryMenuController controller;

  @Override
  public void initialize(InventoryMenuController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return null;
  }
}
