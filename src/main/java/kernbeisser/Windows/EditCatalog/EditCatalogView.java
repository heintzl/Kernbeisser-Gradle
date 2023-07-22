package kernbeisser.Windows.EditCatalog;

import javax.swing.*;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditCatalogView implements IView<EditCatalogController> {

  private JPanel main;
  private ObjectViewView<CatalogEntry> objectView;

  @Linked private EditCatalogController controller;

  public void messageBarcodeNotFound(String s) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Konnte keinen Katalog-Artikel mit Barcode \"" + s + "\" finden",
        "Eintrag nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void pasteInSearchBox(String s) {}

  @Override
  public void initialize(EditCatalogController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public String getTitle() {
    return "Großhandels-Katalog";
  }

  private void createUIComponents() {
    objectView = controller.getObjectView();
  }
}
