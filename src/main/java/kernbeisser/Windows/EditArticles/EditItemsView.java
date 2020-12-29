package kernbeisser.Windows.EditArticles;

import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ObjectView.ObjectViewView;
import org.jetbrains.annotations.NotNull;

public class EditItemsView implements IView<EditItemsController> {

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

  @Override
  public String getTitle() {
    return "Artikel bearbeiten";
  }

  private void createUIComponents() {
    objectView = controller.getObjectView();
  }
}
