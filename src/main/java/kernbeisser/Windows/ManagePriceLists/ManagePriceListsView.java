package kernbeisser.Windows.ManagePriceLists;

import java.util.function.Consumer;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.CustomComponents.ObjectTree.Node;
import kernbeisser.CustomComponents.ObjectTree.ObjectTree;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.Setter;
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
  private JButton print;
  @Setter private JButton editPriceList;

  @Linked private ManagePriceListsController controller;

  @Override
  public void initialize(ManagePriceListsController controller) {
    addPriceList.addActionListener(controller);
    deletePriceList.addActionListener(controller);
    renamePriceList.addActionListener(controller);
    moveArticles.addActionListener(controller);
    moveItems.addActionListener(controller);
    print.addActionListener(controller);
    editPriceList.addActionListener(controller);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    priceLists = new ObjectTree<>(controller.getNode());
    priceLists.addSelectionListener(this::priceListNodeSelection);
    articles =
        new ObjectTable<>(
            Columns.create("Name", Article::getName, SwingConstants.LEFT),
            Columns.create("Lieferant", Article::getSupplier, SwingConstants.LEFT));
  }

  @NotNull
  private void priceListNodeSelection(Node<PriceList> e) {
    boolean isLeaf = e.isLeaf();
    deletePriceList.setEnabled(isLeaf);
    editPriceList.setEnabled(isLeaf);
    if (isLeaf) {
      articles.setObjects(controller.getAllArticles(e.getValue()));
    }
    ;
  }

  Node<PriceList> getSelectedNode() {
    return priceLists.getSelected();
  }

  public Collection<Article> getSelectedArticles() {
    return articles.getSelectedObjects();
  }

  public boolean commitMovement(PriceList from, PriceList to) {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Bist du sicher, dass die ausgewählten Artikel '"
                + from.getName()
                + "',\n"
                + "in die Preisliste '"
                + to.getName()
                + "' verschoben werden sollen?")
        == 0;
  }

  public void getPriceListNode(Consumer<Node<PriceList>> consumer, boolean onlyLeaves) {
    ObjectTree<PriceList> priceListObjectTree = new ObjectTree<>(PriceList.getPriceListsAsNode());
    priceListObjectTree.addSelectionListener(
        e -> {
          if (!onlyLeaves || e.isLeaf()) {
            consumer.accept(e);
            IView.traceViewContainer(priceListObjectTree).requestClose();
          }
        });
    new ComponentController(priceListObjectTree, "Preisliste auswählen")
        .openIn(new SubWindow(traceViewContainer()));
  }
  public void requiresPriceList(Consumer<Node<PriceList>> consumer) {
    getPriceListNode(consumer, false);
  }

  public void requiresPriceListLeaf(Consumer<Node<PriceList>> consumer) {
    getPriceListNode(consumer, true);
  }

  public void selectionRequired() {
    JOptionPane.showMessageDialog(getTopComponent(), "Bitte wähle zunächst eine Preisliste aus.");
  }

  public void refreshNode() {
    articles.setObjects(controller.getAllArticles(getSelectedNode().getValue()));
  }

  public void requestRepaint() {
    priceLists.refresh();
  }

  public void cannotMoveIntoSelf() {
    JOptionPane.showMessageDialog(
        getTopComponent(), "Die Preisliste kann nicht in sich selbst verschoben werden!");
  }

  public String requestName() {
    return JOptionPane.showInputDialog(getTopComponent(), "Bitte gib den Namen ein");
  }

  public boolean commitItemMovement(PriceList from, PriceList to) {
    return JOptionPane.showConfirmDialog(
            getTopComponent(),
            "Bist du sicher, dass die markierten Artikel der Preisliste '"
                + from.getName()
                + "',\n"
                + "in die Preisliste '"
                + (to.getId() == 0 ? "Preislisten" : to.getName())
                + "' verschoben werden sollen?",
            "Preisliste verschieben",
            JOptionPane.OK_CANCEL_OPTION)
        == 0;
  }

  public void cannotDelete() {
    JOptionPane.showMessageDialog(
        getTopComponent(),
        "Die Preisliste kann nicht gelöscht werden, da sie Artikel oder andere Preislisten enthält.");
  }

  public void nameAlreadyExists(String name) {
    JOptionPane.showMessageDialog(getTopComponent(), "Der Name " + name + " existiert bereits.");
  }

  public void warningNoArticlesSelected() {
    JOptionPane.showMessageDialog(
            getTopComponent(),
            "Es sind keine Artikel zum Verschieben ausgewählt!",
            "Artikel verschieben",
            JOptionPane.WARNING_MESSAGE);
  }

  @Override
  public String getTitle() {
    return "Preislisten bearbeiten";
  }
}
