package kernbeisser.Windows.ShoppingMask.ArticleSelector;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Locale;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
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
  private SearchBoxView<Article> searchBox;

  @Linked
  private ArticleSelectorController controller;

  @Linked
  private SearchBoxController<Article> searchBoxController;

  private void createUIComponents() {
    searchBox = searchBoxController.getView();
  }

  @Override
  public void initialize(ArticleSelectorController controller) {
    chooseButton.addActionListener(e -> controller.choose());
  }

  @Override
  public @NotNull Dimension getSize() {
    return new Dimension(700, 600);
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
