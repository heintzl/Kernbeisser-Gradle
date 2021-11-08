package kernbeisser.Windows.Inventory;

import javax.swing.*;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class InventoryView implements IView<InventoryController> {
  private JPanel main;
  private ObjectViewView<Shelf> shelfView;

  @Linked private ObjectViewController<Shelf> shelfViewController;

  @Override
  public void initialize(InventoryController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    shelfView = shelfViewController.getView();
  }
}
