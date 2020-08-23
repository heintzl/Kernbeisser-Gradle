package kernbeisser.Windows.EditItems;

import javax.swing.*;

import kernbeisser.CustomComponents.BarcodeCapture;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import kernbeisser.Windows.ObjectView.ObjectViewView;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;

public class EditItemsView implements View<EditItemsController> {

  private JPanel main;
  private JButton choosePriceList;
  private ObjectViewView<Article> objectView;

  @Linked private EditItemsController controller;

  @Linked private BarcodeCapture capture;

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

  @Override
  public boolean processKeyboardInput(KeyEvent e) {
    return capture.processKeyEvent(e);
  }
}
