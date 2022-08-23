package kernbeisser.Windows.ManagePriceLists;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Collection;
import java.util.Optional;
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
            Columns.create("Lieferant", Article::getSupplier, SwingConstants.LEFT));
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

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    main = new JPanel();
    main.setLayout(new GridLayoutManager(2, 2, new Insets(5, 5, 5, 5), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel1,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(
        panel2,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel2.add(
        scrollPane1,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    scrollPane1.setViewportView(priceLists);
    treeButtonPanel = new JPanel();
    treeButtonPanel.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
    panel2.add(
        treeButtonPanel,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    addPriceList = new JButton();
    addPriceList.setActionCommand("add");
    addPriceList.setText("Preisliste Hinzufügen");
    treeButtonPanel.add(
        addPriceList,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    deletePriceList = new JButton();
    deletePriceList.setActionCommand("delete");
    deletePriceList.setText("Preisliste Löschen");
    treeButtonPanel.add(
        deletePriceList,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    renamePriceList = new JButton();
    renamePriceList.setActionCommand("rename");
    renamePriceList.setText("Preisliste Umbennen");
    treeButtonPanel.add(
        renamePriceList,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    movePriceList = new JButton();
    movePriceList.setActionCommand("move");
    movePriceList.setText("Preisliste verschieben");
    treeButtonPanel.add(
        movePriceList,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    main.add(
        panel3,
        new GridConstraints(
            1,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    contentButtonPanel = new JPanel();
    contentButtonPanel.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(
        contentButtonPanel,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    contentButtonPanel.add(
        spacer1,
        new GridConstraints(
            0,
            5,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            1,
            null,
            null,
            null,
            0,
            false));
    moveArticles = new JButton();
    moveArticles.setActionCommand("move_items");
    moveArticles.setText("Artikel aus der Preisliste verschieben");
    contentButtonPanel.add(
        moveArticles,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    print = new JButton();
    print.setActionCommand("print");
    print.setText("Drucken");
    contentButtonPanel.add(
        print,
        new GridConstraints(
            0,
            4,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    editPriceList = new JButton();
    editPriceList.setActionCommand("edit");
    editPriceList.setText("Preisliste bearbeiten");
    contentButtonPanel.add(
        editPriceList,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    addArticle = new JButton();
    addArticle.setText("Artikel Hinzufügen");
    contentButtonPanel.add(
        addArticle,
        new GridConstraints(
            0,
            2,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    editArticle = new JButton();
    editArticle.setText("Artikel Bearbeiten");
    contentButtonPanel.add(
        editArticle,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JScrollPane scrollPane2 = new JScrollPane();
    panel3.add(
        scrollPane2,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    scrollPane2.setViewportView(articles);
    final JLabel label1 = new JLabel();
    label1.setText("Aktuelle Artikel der Preiliste");
    main.add(
        label1,
        new GridConstraints(
            0,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final JLabel label2 = new JLabel();
    label2.setText("Preisliste");
    main.add(
        label2,
        new GridConstraints(
            0,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
