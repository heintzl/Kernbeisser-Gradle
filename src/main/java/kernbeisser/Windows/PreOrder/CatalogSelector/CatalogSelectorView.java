package kernbeisser.Windows.PreOrder.CatalogSelector;

import java.awt.*;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.RowFilter;
import kernbeisser.CustomComponents.SearchBox.SearchBoxController;
import kernbeisser.CustomComponents.SearchBox.SearchBoxView;
import kernbeisser.DBEntities.Article;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class CatalogSelectorView implements IView<CatalogSelectorController> {

  private JPanel main;
  private JButton chooseButton;
  private SearchBoxView<Article> searchBox;

  @Linked private CatalogSelectorController controller;

  @Linked private SearchBoxController<Article> searchBoxController;

  private void createUIComponents() {
    searchBox = searchBoxController.getView();
  }

  @Override
  public void initialize(CatalogSelectorController controller) {
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
