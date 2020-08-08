package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import java.awt.*;
import javax.swing.*;

import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.MVC.View;
import org.jetbrains.annotations.NotNull;

public class ArticleSelectorView implements View<ArticleSelectorController> {
  private JPanel main;
  private JButton chooseButton;
  private JCheckBox onlyWithoutBarcode;
  private SearchBoxView<Article> searchBox;
  private JCheckBox showInShopArticles;

  @Linked
  private SearchBoxController<Article> searchBoxController;

  boolean searchOnlyWithoutBarcode() {
    return onlyWithoutBarcode.isSelected();
  }

  boolean searchOnlyShowInShop() {
    return showInShopArticles.isSelected();
  }

  private void createUIComponents() {
    searchBox = searchBoxController.getView();
  }

  @Override
  public void initialize(ArticleSelectorController controller) {
    onlyWithoutBarcode.addActionListener(
        e -> {
          controller.refreshLoadSolutions();
          showInShopArticles.setSelected(false);
        });
    showInShopArticles.addActionListener(
        e -> {
          controller.refreshLoadSolutions();
          onlyWithoutBarcode.setSelected(false);
        });
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
}
