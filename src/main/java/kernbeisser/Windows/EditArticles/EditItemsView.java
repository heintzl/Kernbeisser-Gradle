package kernbeisser.Windows.EditArticles;

import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ObjectView.ObjectViewView;
import org.jetbrains.annotations.NotNull;

public class EditItemsView implements IView<EditItemsController> {

  private JPanel main;
  private JButton choosePriceList;
  private ObjectViewView<Article> objectView;
  private JCheckBox showShopRange;

  @Linked private EditItemsController controller;

  boolean showOnlyShopRange() {
    return showShopRange != null && showShopRange.isSelected();
  }

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
    showShopRange.setSelected(true);
    showShopRange.addActionListener(e -> controller.refreshList());
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
