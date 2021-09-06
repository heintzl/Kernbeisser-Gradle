package kernbeisser.Windows.EditArticles;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditItemsView implements IView<EditItemsController> {

  private JPanel main;
  private JButton choosePriceList;
  private ObjectViewView<Article> objectView;

  @Linked
  private EditItemsController controller;

  public void messageBarcodeNotFound(String s) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Konnte keinen Artikel mit Barcode \"" + s + "\" finden",
        "Artikel nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }

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
