package kernbeisser.Windows.ManagePriceLists;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Column;
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
  private JButton movePriceList;
  private ObjectTree<PriceList> priceLists;
  private JPanel main;
  private JButton moveArticles;
  private JButton print;
  @Setter private JButton editPriceList;
  private JPanel treeButtonPanel;
  private JPanel contentButtonPanel;
  private JButton addArticle;
  private JButton editArticle;

  @Linked private ManagePriceListsController controller;

  @Override
  public void initialize(ManagePriceListsController controller) {
    priceLists.addSelectionChangeListener(
        node -> {
          if (isNonRoot(node)) articles.setObjects(controller.getAllArticles(node.getValue()));
          else articles.clear();
        });
    priceLists.selectionComponents(
        this::isNonRoot, deletePriceList, renamePriceList, movePriceList);
    priceLists.selectionComponents(this::nonEmptyPriceList, print, moveArticles);
    priceLists.selectionComponents(Node::isLeaf, deletePriceList, editPriceList, addArticle);
    addPriceList.addActionListener(controller);
    deletePriceList.addActionListener(controller);
    renamePriceList.addActionListener(controller);
    movePriceList.addActionListener(controller);
    editPriceList.addActionListener(controller);
    articles.selectionComponent(editArticle);
    articles.addDoubleClickListener(controller::editArticle);
    editArticle.addActionListener(controller::editSelectedArticle);
    addArticle.addActionListener(controller::addArticle);
    moveArticles.addActionListener(controller);
    print.addActionListener(controller);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    priceLists = new ObjectTree<>(controller.getNode());
    articles =
        new ObjectTable<>(
            Columns.create("Name", Article::getName, SwingConstants.LEFT),
            Columns.create("Lieferant", Article::getSupplier, SwingConstants.LEFT),
            Columns.create("Lieferanten Nr.", Article::getSuppliersItemNumber)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create(
                "Aufschlagsgruppe",
                (Article e) ->
                    String.format(
                        "%s(%.2f%%)",
                        e.getSurchargeGroup().getName(), e.getSurchargeGroup().getSurcharge())));
  }

  private Optional<Article> getSelectedArticle() {
    return articles.getSelectedObject();
  }

  private void messageNoArticleSelected() {
    message("Um einen Artikel zu bearbeiten, muss zunächst ein Artikel ausgewählt werden.");
  }

  private boolean isNonRoot(Node<PriceList> node) {
    return node.getValue().getId() != 0;
  }

  private boolean nonEmptyPriceList(Node<PriceList> node) {
    return !controller.getAllArticles(node.getValue()).isEmpty();
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

  public void requestPriceListSelection(Consumer<Node<PriceList>> consumer, boolean onlyLeaves) {
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

  public void requestPriceListSelection(Consumer<Node<PriceList>> consumer) {
    requestPriceListSelection(consumer, false);
  }

  public void requiresPriceListLeaf(Consumer<Node<PriceList>> consumer) {
    requestPriceListSelection(consumer, true);
  }

  public void messageSelectionRequired() {
    message("Bitte wähle zunächst eine Preisliste aus.");
  }

  public void refreshNode() {
    articles.setObjects(controller.getAllArticles(getSelectedNode().getValue()));
  }

  public void requestRepaint() {
    priceLists.refresh();
  }

  public void cannotMoveIntoSelf() {
    message(
        "Die Preisliste kann nicht in sich selbst verschoben werden!",
        "Preisliste kann nicht verschoben werden.",
        JOptionPane.WARNING_MESSAGE);
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
    message(
        "Die Preisliste kann nicht gelöscht werden, da sie Artikel oder andere Preislisten enthält.");
  }

  public void nameAlreadyExists(String name) {
    message("Der Name " + name + " existiert bereits.");
  }

  public void warningNoArticlesSelected() {
    message(
        "Es sind keine Artikel zum Verschieben ausgewählt!",
        "Artikel verschieben",
        JOptionPane.WARNING_MESSAGE);
  }

  @Override
  public String getTitle() {
    return "Preislisten bearbeiten";
  }
}
