package kernbeisser.Windows.PrintLabels;

import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PrintLabelsView implements IView<PrintLabelsController> {

  private JPanel main;
  private CollectionView collectionView;
  private JButton printSheetInfo;

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
            "Sollen die ausgewählten Etiketten gedruckt werden?",
            "Wechsel der bearbeiteten Rolle",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.YES_OPTION;
  }

  @Override
  public String getTitle() {
    return "Etiketten Zusammenstellen";
  }

  public void refreshView() {}
}
