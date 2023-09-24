package kernbeisser.Windows.EditArticles;

import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditArticlesView implements IView<EditArticlesController> {

  private JPanel main;
  private JButton choosePriceList;
  private ObjectViewView<Article> objectView;

  @Linked private EditArticlesController controller;

  public void messageBarcodeNotFound(String s) {
    Tools.beep();
    JOptionPane.showMessageDialog(
        getContent(),
        "Konnte keinen Artikel mit Barcode \"" + s + "\" finden",
        "Artikel nicht gefunden",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void messageNoDifferences() {
    message(
        "Es gibt keine bekannten Differenzen. Du musst erst welche angezeigen, bevor sie übernommen werden können.",
        "Fehlende Differenz-Daten");
  }

  public void messageNoSelection() {
    message(
        "Du musst die Artikel auswählen, für die die Katalogdaten übernommen werden sollen.",
        "Fehlende Artikel-Auswahl");
  }

  @Override
  public void initialize(EditArticlesController controller) {
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
