package kernbeisser.Windows.ManagePriceLists;

import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class ManagePriceListsView implements IView<ManagePriceListsController> {
  private JButton addPriceList;
  private JButton deletePriceList;
  private JButton renamePriceList;
  private ObjectTable<Article> articles;
  private JButton moveArticles;
  private ObjectTree<PriceList> priceLists;
  private JPanel main;

  @Linked private ManagePriceListsController controller;

  @Override
  public void initialize(ManagePriceListsController controller) {
    addPriceList.addActionListener(controller);
    deletePriceList.addActionListener(controller);
    renamePriceList.addActionListener(controller);
    moveArticles.addActionListener(controller);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    priceLists = new ObjectTree<>(controller.getNode());
    articles =
        new ObjectTable<>(
            Column.create("Name", Article::getName),
            Column.create("Lieferant", Article::getSupplier));
  }

  Node<PriceList> getSelectedNode() {
    return priceLists.getSelected();
  }
}
