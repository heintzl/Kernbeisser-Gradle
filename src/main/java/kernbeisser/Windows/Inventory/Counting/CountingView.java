package kernbeisser.Windows.Inventory.Counting;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.swing.*;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.Article;
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
  private ArticleStock stockBefore;
  JLabel inventoryDate;

  @Linked private CountingController controller;

  @Override
  public void initialize(CountingController controller) {
    shelf.addSelectionListener(controller::loadShelf);
    apply.addActionListener(e -> applyAmount());
    amount.addActionListener(e -> applyAmount());
    addArticle.addActionListener(e -> controller.addArticleStock());
    articleStocks.addSelectionListener(this::stockSelectionChanged);
    articleStocks.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    commit.addActionListener(e -> close());
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
            Columns.<ArticleStock>create(
                    "Gezählte Menge", (e -> String.format("%.1f", e.getCounted())))
                .withSorter(Column.NUMBER_SORTER));
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

  private void saveStockBefore() {
    double count = amount.getSafeValue();
    if (stockBefore != null && stockBefore.getCounted() != count) {
      controller.setStock(stockBefore, count);
    }
  }

  void stockSelectionChanged(ArticleStock stock) {
    saveStockBefore();
    loadArticleStock(stock);
  }

  void loadArticleStock(ArticleStock stock) {
    stockBefore = stock;
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
      articleStocks.selectRow(0);
      ArticleStock stock = articleStocks.getSelectedObject().orElse(null);
      loadArticleStock(stock);
    }
  }

  public void selectArticle(Article article) {
    int rowIndex = 0;
    for (ArticleStock stock : articleStocks.getObjects()) {
      if (stock.getArticle().equals(article)) {
        loadArticleStock(stock);
        break;
      }
      rowIndex++;
    }
    articleStocks.selectRow(rowIndex);
  }

  public void refreshArticleStock(ArticleStock articleStock) {
    articleStocks.replace(articleStock, articleStock);
  }

  private void close() {
    saveStockBefore();
    back();
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

  public void setInventoryDate(String dateString) {
    this.inventoryDate.setText(dateString);
  }

  public void formatInventoryDateAsWarning() {
    this.inventoryDate.setForeground(Color.RED);
    Font font =
        this.inventoryDate
            .getFont()
            .deriveFont(
                Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD));
    this.inventoryDate.setFont(font);
  }

  public void formatInventoryDateAsMessage() {
    this.inventoryDate.setForeground(Color.BLACK);
    Font font =
        this.inventoryDate
            .getFont()
            .deriveFont(
                Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD));
    this.inventoryDate.setFont(font);
  }
}
