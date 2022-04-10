package kernbeisser.Windows.EditPriceList;

import javax.swing.*;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class EditPriceListView implements IView<EditPriceListController> {

  private JPanel main;
  private CollectionView collectionView;
  private PriceList priceList;

  @Linked private CollectionController<Article> articles;

  @Override
  public void initialize(EditPriceListController controller) {
    priceList = controller.getPricelist();
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    collectionView = articles.getView();
  }

  public int confirmChanges(int movedItems, int notListed) {
    String message = "Sollen die Änderungen an der Preisliste gespeichert werden?";
    if (movedItems != 0 || notListed != 0) {
      message += "\nACHTUNG: ";
      if (movedItems != 0) {
        message +=
            movedItems
                + " Artikel "
                + (movedItems == 1 ? "wird" : "werden")
                + "dadurch in eine andere Liste verschoben"
                + (notListed != 0 ? " und" : "");
      }
      if (notListed != 0) {
        message +=
            notListed
                + " Artikel "
                + (notListed == 1 ? "steht" : "stehen")
                + " dann auf keiner Liste mehr";
      }
      message += "!";
    }
    return JOptionPane.showConfirmDialog(
        getContent(),
        message,
        "Änderungen speichern",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE);
  }

  @Override
  public String getTitle() {
    return "Preisliste " + priceList.getName() + "  bearbeiten";
  }

  public void refreshView() {}
}
