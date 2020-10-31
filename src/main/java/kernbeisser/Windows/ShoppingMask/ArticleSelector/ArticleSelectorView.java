package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import java.awt.*;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ArticleSelectorView implements IView<ArticleSelectorController> {
  private JPanel main;
  private JButton chooseButton;
  private JCheckBox onlyWithoutBarcode;
  private SearchBoxView<Article> searchBox;
  private JCheckBox showInShopArticles;

  @Linked private ArticleSelectorController controller;

  @Linked private SearchBoxController<Article> searchBoxController;

  boolean searchOnlyWithoutBarcode() {
    return onlyWithoutBarcode != null && onlyWithoutBarcode.isSelected();
  }

  boolean searchOnlyShowInShop() {
    return showInShopArticles != null && showInShopArticles.isSelected();
  }

  private void createUIComponents() {
    searchBox = searchBoxController.getView();
  }

  @Override
  public void initialize(ArticleSelectorController controller) {
    onlyWithoutBarcode.addActionListener(e -> controller.refreshSearch());
    showInShopArticles.addActionListener(e -> controller.refreshSearch());
    chooseButton.addActionListener(e -> controller.choose());
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(500, 600);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  @Override
  public Component getFocusOnInitialize() {
    return searchBoxController.getView().getFocusOnInitialize();
  }

  public void setRowFilter(RowFilter<Article> articleRowFilter) {
    searchBox.setRowFilter(articleRowFilter);
  }
}
