package kernbeisser.Windows.Inventory.Counting;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CountingView implements IView<CountingController> {
  private JPanel main;
  private JButton commit;
  private kernbeisser.CustomComponents.TextFields.DoubleParseField amount;
  private JButton apply;
  private JLabel articleName;
  private ObjectTable<ArticleStock> articleStocks;
  private AdvancedComboBox<Shelf> shelf;
  private JLabel articleNumber;
  private JButton addArticle;

  @Linked private CountingController controller;

  @Override
  public void initialize(CountingController controller) {
    shelf.addSelectionListener(controller::loadShelf);
    apply.addActionListener(e -> applyAmount());
    amount.addActionListener(e -> applyAmount());
    addArticle.addActionListener(e -> controller.addArticleStock());
    articleStocks.addSelectionListener(this::loadArticleStock);
    articleStocks.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    commit.addActionListener(e -> back());
  }

  Optional<Shelf> getSelectedShelf() {
    return shelf.getSelected();
  }

  void setSelectedShelf(Shelf shelf) {
    this.shelf.setSelectedItem(shelf);
  }

  void setArticleStocks(Collection<ArticleStock> articleStocks) {
    this.articleStocks.setObjects(articleStocks);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    articleStocks =
        new ObjectTable<>(
            Columns.<ArticleStock>create("Artikelnummer", e -> e.getArticle().getKbNumber())
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Artikelname", e -> e.getArticle().getName()),
            Columns.create("Gezählte Menge", ArticleStock::getCounted));
    shelf = new AdvancedComboBox<>(e -> e.getShelfNo() + " - " + e.getLocation());
  }

  void applyAmount() {
    int selectedIndex = articleStocks.getSelectionModel().getMinSelectionIndex();
    articleStocks.getSelectedObject().ifPresent(e -> controller.setStock(e, amount.getSafeValue()));
    if (selectedIndex == articleStocks.getObjects().size()) {
      Tools.beep();
      return;
    }
    articleStocks.getSelectionModel().setSelectionInterval(selectedIndex + 1, selectedIndex + 1);
    articleStocks.getSelectedObject().ifPresent(this::loadArticleStock);
  }

  void loadArticleStock(ArticleStock stock) {
    amount.setText(String.valueOf(stock.getCounted()));
    articleName.setText(stock.getArticle().getName());
    articleNumber.setText(String.valueOf(stock.getArticle().getKbNumber()));
    amount.requestFocus();
    amount.setSelectionStart(0);
    amount.setSelectionEnd(amount.getText().length());
  }

  public void setShelves(Collection<Shelf> allShelves) {
    shelf.setItems(allShelves);
  }

  public void selectFirst() {
    if (!articleStocks.getObjects().isEmpty()) {
      articleStocks.getSelectionModel().setSelectionInterval(0, 0);
    }
  }

  @Override
  public IconCode getTabIcon() {
    return FontAwesome.LIST;
  }

  @Override
  @StaticAccessPoint
  public String getTitle() {
    return "Zähl Ergebnisse eingeben";
  }
}
