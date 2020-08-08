package kernbeisser.Windows.EditItems;

import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import kernbeisser.Windows.ObjectView.ObjectViewView;
import org.jetbrains.annotations.NotNull;

public class EditItemsView implements View<EditItemsController> {

  private JPanel main;
  private JButton choosePriceList;
  private ObjectViewView<Article> objectView;

  @Linked private EditItemsController controller;

  @Override
  public void initialize(EditItemsController controller) {
    choosePriceList.addActionListener(e -> controller.openPriceListSelection());
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    objectView = controller.getObjectView();
  }
}
