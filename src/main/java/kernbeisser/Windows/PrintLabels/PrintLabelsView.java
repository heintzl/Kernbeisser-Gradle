package kernbeisser.Windows.PrintLabels;

import jakarta.persistence.NoResultException;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PrintLabelsView implements IView<PrintLabelsController> {

  private JPanel main;
  private CollectionView collectionView;

  @Linked private CollectionController<Article> articles;

  @Override
  public void initialize(PrintLabelsController controller) {}

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    collectionView = articles.getView();
  }

  public boolean confirmChanges() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Soll ich mir die ausgewählten Etiketten für den Ausdruck merken?",
            "Später drucken",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.YES_OPTION;
  }

  public boolean confirmPrintSuccess() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Sollen die gedruckten Etiketten aus der Zusammenstellung entfernt werden?",
            "Etiketten zusammenstellen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.YES_OPTION;
  }

  public void refreshChosenArticle(Article article) {
    articles.getView().getChosen().replace(article, article);
  }

  public void processBarcode(String s) {
    try {
      Article article = PrintLabelsModel.getByBarcode(s);
      CollectionView<Article> articleCollectionView = articles.getView();
      ObjectTable<Article> objectTable = articleCollectionView.getChosen();
      int row = objectTable.getModel().getObjects().indexOf(article);
      if (row == -1) {
        objectTable = articleCollectionView.getAvailable();
        row = objectTable.getModel().getObjects().indexOf(article);
      }
      if (row == -1) return;
      objectTable.selectRow(row);
      objectTable.requestFocusInWindow();
    } catch (NoResultException e) {
      Tools.noArticleFoundForBarcodeWarning(getContent(), s);
    }
  }

  @Override
  public String getTitle() {
    return "Etiketten Zusammenstellen";
  }
}
