package kernbeisser.Windows.ManagePriceLists;

import java.util.function.Consumer;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ViewContainers.SubWindow;
import org.jetbrains.annotations.NotNull;

public class ManagePriceListsView implements IView<ManagePriceListsController> {
  private JButton addPriceList;
  private JButton deletePriceList;
  private JButton renamePriceList;
  private ObjectTable<Article> articles;
  private JButton moveArticles;
  private ObjectTree<PriceList> priceLists;
  private JPanel main;
  private JButton moveItems;

  @Linked private ManagePriceListsController controller;

  @Override
  public void initialize(ManagePriceListsController controller) {
    addPriceList.addActionListener(controller);
    deletePriceList.addActionListener(controller);
    renamePriceList.addActionListener(controller);
    moveArticles.addActionListener(controller);
    moveItems.addActionListener(controller);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    priceLists = new ObjectTree<>(controller.getNode());
    priceLists.addSelectionListener(
        e -> articles.setObjects(controller.getAllArticles(e.getValue())));
    articles =
        new ObjectTable<>(
            Column.create("Name", Article::getName, SwingConstants.LEFT),
            Column.create("Lieferant", Article::getSupplier, SwingConstants.LEFT));
  }

  Node<PriceList> getSelectedNode() {
    return priceLists.getSelected();
  }

  public boolean commitMovement(PriceList from, PriceList to) {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Sind sie sich sicher, das die Preisliste '"
                + from.getName()
                + "',\nin die Preisliste '"
                + to.getName()
                + "' verschoben werden soll?")
        == 0;
  }

  public void requiresPriceList(Consumer<Node<PriceList>> consumer) {
    ObjectTree<PriceList> priceListObjectTree = new ObjectTree<>(PriceList.getPriceListsAsNode());
    priceListObjectTree.addSelectionListener(
        e -> {
          consumer.accept(e);
          IView.traceViewContainer(priceListObjectTree.getParent()).requestClose();
        });
    new ComponentController(priceListObjectTree, "Preisliste auswählen")
        .openIn(new SubWindow(traceViewContainer()));
  }

  public void selectionRequired() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Bitte wählen sie zunächst eine Preisliste aus.");
  }

  public void requestRepaint() {
    priceLists.refresh();
  }

  public void cannotMoveIntoSelf() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Die Preisliste kann nicht in sich selbst verschoben werden!");
  }

  public String requestName() {
    return JOptionPane.showInputDialog(getTopComponent(), "Bitte geben sie den Namen ein");
  }

  public boolean commitItemMovement(PriceList from, PriceList to) {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Sind sie sich sicher, das die Artikel der Preisliste '"
                + from.getName()
                + "',\nin die Preisliste '"
                + to.getName()
                + "' verschoben werden soll?")
        == 0;
  }

  public void cannotDelete() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Die Preisliste kann nicht gelöscht werden, da Artikel oder Preislisten auf diese verweissen.");
  }

  public void nameAlreadyExists(String name) {
    JOptionPane.showMessageDialog(getTopComponent(), "Der Name " + name + " existiert bereits.");
  }

  @Override
  public String getTitle() {
    return "Preislisten bearbeiten";
  }
}
