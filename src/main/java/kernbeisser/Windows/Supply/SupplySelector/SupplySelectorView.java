package kernbeisser.Windows.Supply.SupplySelector;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.Dialogs.SelectionDialog;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class SupplySelectorView implements IView<SupplySelectorController> {

  private JPanel main;
  private ObjectTable<Supply> supplySelector;
  private JButton deleteSupply;
  private JButton export;
  private ObjectTable<LineContent> lineContents;
  private AdvancedComboBox<ResolveStatus> filter;
  private JButton printProduce;
  private JButton verifyArticlesButton;
  private JProgressBar progressBar1;
  private JPanel loadingIndicator;
  private JButton viewOrders;

  @Override
  public void initialize(SupplySelectorController controller) {
    supplySelector.addSelectionListener(e -> lineContents.setObjects(e.getAllLineContents()));
    filter.getRenderer().setIconFunction(this::getIcon);
    filter.addActionListener(
        e -> {
          lineContents.setRowFilter(
              lineContent ->
                  filter
                      .getSelected()
                      .map(status -> status == lineContent.getStatus())
                      .orElse(true));
        });
    filter.getModel().setAllowNullSelection(true);
    filter
        .getRenderer()
        .setNoSelectionIcon(
            IconFontSwing.buildIcon(
                FontAwesome.QUESTION, Tools.scaleWithLabelScalingFactor(15), Color.RED));
    filter.getRenderer().setNoSelectionText("Alle Artikel");
    deleteSupply.addActionListener(controller::deleteCurrentSupply);
    export.addActionListener(e -> controller.exportShoppingItems());
    printProduce.addActionListener(e -> controller.printProduce());
    verifyArticlesButton.addActionListener(this::verifyArticle);
    viewOrders.addActionListener(controller::viewOrders);
  }

  private void verifyArticle(ActionEvent actionEvent) {
    String artNrStr = JOptionPane.showInputDialog("Artikel:");
    if (artNrStr == null) return;
    int artNr = Integer.parseInt(artNrStr);
    Collection<LineContent> found = new ArrayList<>();
    for (LineContent content : lineContents) {
      if (content.getKkNumber() == artNr) {
        found.add(content);
      }
    }
    SelectionDialog.select(getTopComponent(), "Artikel:", found)
        .ifPresent(
            e -> {
              message(e.toString());
              e.verify();
            });
    lineContents.getModel().fireTableDataChanged();
    verifyArticle(actionEvent);
  }

  public void setFilterOptions(Collection<ResolveStatus> filters) {
    this.filter.setItems(filters);
    filter.setSelectedItem(null);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  public void setSupplies(Collection<Supply> supplies) {
    supplySelector.setObjects(supplies);
  }

  private void createUIComponents() {
    supplySelector =
        new ObjectTable<Supply>(
            Columns.create(
                "Datum",
                e -> Date.INSTANT_DATE_TIME.format(e.getDeliveryDate()),
                SwingConstants.LEFT,
                Comparator.comparing(
                    ((Object s) -> Instant.from(Date.INSTANT_DATE_TIME.parse((String) s))))),
            Columns.create("Anzahl Artikel", Supply::getArticleCount)
                .withRightClickConsumer(this::listSupplyFiles),
            Columns.create("Summe", e -> String.format("%.2f€", e.getContentSum())));
    lineContents =
        new ObjectTable<LineContent>(
            Columns.create("Artikelnr.", LineContent::getKkNumber),
            Columns.create("Artikelname", LineContent::getName),
            Columns.create("Geb.preis", LineContent::getPrice),
            Columns.create("Geb.größe", LineContent::getContainerSize),
            Columns.create(
                "Packung",
                e -> e.getAmount() + (e.getUnit() == null ? "" : e.getUnit().getShortName())),
            Columns.create("Kommentar", LineContent::getMessage),
            Columns.create(
                "Preis",
                e -> String.format("%.2f€", e.getTotalPrice()),
                SwingUtilities.RIGHT,
                Column.NUMBER_SORTER),
            Columns.<LineContent>createIconColumn("S", e -> getIcon(e.getStatus()))
                .withColumnAdjustor(
                    column -> column.setMaxWidth(Tools.scaleWithLabelScalingFactor(20))),
            Columns.<LineContent>createIconColumn(
                "", e -> e.isVerified() ? verified : notVerified));
  }

  private void listSupplyFiles(Supply supply) {
    ObjectTable<SupplierFile> supplierFiles =
        new ObjectTable<>(Columns.create("Auftrag", e -> e.getHeader().getOrderNr()));
    supplierFiles.setObjects(supply.getSupplierFiles());
    new ComponentController(supplierFiles).openTab();
  }

  private static final Icon okIcon = createIcon(FontAwesome.CHECK_CIRCLE, new Color(0x238678));
  private static final Icon addedIcon = createIcon(FontAwesome.PLUS, new Color(0xB648BA));
  private static final Icon ignoreIcon = createIcon(FontAwesome.EXCLAMATION_CIRCLE, Color.ORANGE);
  private static final Icon produceIcon = createIcon(FontAwesome.LEAF, new Color(0x0D5C0A));

  private static final Icon verified = okIcon;
  private static final Icon notVerified = ignoreIcon;

  private static Icon createIcon(FontAwesome icon, Color color) {
    return IconFontSwing.buildIcon(icon, Tools.scaleWithLabelScalingFactor(15), color);
  }

  private Icon getIcon(ResolveStatus status) {
    switch (status) {
      case OK:
        return okIcon;
      case ADDED:
        return addedIcon;
      case IGNORE:
        return ignoreIcon;
      case PRODUCE:
        return produceIcon;
      default:
        throw new IllegalArgumentException("Status has no icon: " + status);
    }
  }

  public Optional<Supply> getSelectedSupply() {
    return supplySelector.getSelectedObject();
  }

  public void messageCommitDelete() {
    if (JOptionPane.showConfirmDialog(
            getContent(),
            "Willst du diese wirklich Lieferung löschen? Dieser Vorgang kann nicht rückgänging gemacht werden!",
            "Potezieller Datenverlusst",
            JOptionPane.YES_NO_OPTION)
        != 0) {
      throw new CancellationException();
    }
  }

  public void setLoadingIndicatorVisible(boolean b) {
    loadingIndicator.setVisible(b);
  }

  public void messageSelectSupplyFirst() {
    message("Bitte wähle zunächst eine Lieferung aus!", "Keine Lieferung ausgewählt!");
  }

  public void messageDefaultDirNotFound() {
    message(
        "Das Standardverzeichnis konnte nicht gefunden werden!\nIst der USB-Stick eingesteckt?",
        "Standardverzeichnis nicht gefunden");
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
    main.setLayout(new GridLayoutManager(3, 2, new Insets(5, 5, 5, 5), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
    panel1.setAlignmentX(0.2f);
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
            new Dimension(700, -1),
            0,
            false));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel1.add(
        scrollPane1,
        new GridConstraints(
            0,
            0,
            1,
            3,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            new Dimension(300, -1),
            null,
            0,
            false));
    scrollPane1.setViewportView(supplySelector);
    deleteSupply = new JButton();
    deleteSupply.setText("Ausgewählte Lieferung löschen");
    panel1.add(
        deleteSupply,
        new GridConstraints(
            2,
            0,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    loadingIndicator = new JPanel();
    loadingIndicator.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    loadingIndicator.setVisible(false);
    panel1.add(
        loadingIndicator,
        new GridConstraints(
            1,
            0,
            1,
            3,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            null,
            null,
            null,
            0,
            false));
    final JLabel label1 = new JLabel();
    label1.setText("Odner wird gelesen...");
    loadingIndicator.add(
        label1,
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
    progressBar1 = new JProgressBar();
    progressBar1.setIndeterminate(true);
    loadingIndicator.add(
        progressBar1,
        new GridConstraints(
            1,
            0,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    final Spacer spacer1 = new Spacer();
    panel1.add(
        spacer1,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_VERTICAL,
            1,
            GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    viewOrders = new JButton();
    viewOrders.setText("Aufträge betrachten");
    panel1.add(
        viewOrders,
        new GridConstraints(
            2,
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
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
    panel2.setAlignmentX(1.0f);
    main.add(
        panel2,
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
    final JScrollPane scrollPane2 = new JScrollPane();
    panel2.add(
        scrollPane2,
        new GridConstraints(
            1,
            0,
            1,
            4,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
            null,
            null,
            null,
            0,
            false));
    scrollPane2.setViewportView(lineContents);
    export = new JButton();
    export.setText("Lieferung übernehmen");
    panel2.add(
        export,
        new GridConstraints(
            2,
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
    final Spacer spacer2 = new Spacer();
    panel2.add(
        spacer2,
        new GridConstraints(
            2,
            0,
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
    final JLabel label2 = new JLabel();
    label2.setText("Filter:");
    panel2.add(
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
    filter = new AdvancedComboBox();
    panel2.add(
        filter,
        new GridConstraints(
            0,
            3,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null,
            0,
            false));
    printProduce = new JButton();
    printProduce.setText("Obst und Gemüse Verkaufspreise drucken");
    panel2.add(
        printProduce,
        new GridConstraints(
            2,
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
    verifyArticlesButton = new JButton();
    verifyArticlesButton.setText("Verify Articles");
    panel2.add(
        verifyArticlesButton,
        new GridConstraints(
            2,
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
    final JLabel label3 = new JLabel();
    label3.setText("Artikel der Lieferung");
    main.add(
        label3,
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
    final JLabel label4 = new JLabel();
    label4.setText("Lieferungen");
    main.add(
        label4,
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
    final Spacer spacer3 = new Spacer();
    main.add(
        spacer3,
        new GridConstraints(
            2,
            1,
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
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() {
    return main;
  }
}
