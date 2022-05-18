package kernbeisser.Windows.PrintLabels;

import java.text.MessageFormat;
import javax.swing.*;
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

  public void refreshChosenArticle(Article article) {
    articles.getView().getChosen().replace(article, article);
  }

  public String inputNumber(int amount, boolean retry) {
    String initValue = MessageFormat.format("{0, number, 0}", amount).trim();
    String message = "";
    String response = "";
    if (retry) { // item is piece, first try
      message = "Die Eingabe ist ungültig. Bitte hier eine gültige Anzahl > 0 eingeben:";
    } else { // item is piece later try
      message = "Bitte neue Anzahl eingeben:";
    }
    Tools.beep();
    response =
        (String)
            JOptionPane.showInputDialog(
                getContent(),
                message,
                "Anzahl anpassen",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                initValue);
    if (response != null) {
      response = response.trim();
    }
    return response;
  }

  @Override
  public String getTitle() {
    return "Etiketten Zusammenstellen";
  }

  public void refreshView() {}
}
