package kernbeisser.Windows.Supply.SupplySelector;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.*;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Predicate;
import javax.swing.*;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Tasks.ArticleComparedToCatalogEntry;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.ComponentController.ComponentController;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class SupplySelectorView implements IView<SupplySelectorController> {

  private JPanel main;
  private ObjectTable<Supply> supplySelector;
  private JButton deleteSupply;
  private JButton export;
  private ObjectTable<LineContent> lineContents;
  private AdvancedComboBox<ResolveStatus> filter;
  private JButton printProduce;
  private JProgressBar progressBar1;
  private JPanel loadingIndicator;
  private JButton viewOrders;
  private JButton openArticle;
  private JButton mergeArticle;
  @Linked private SupplySelectorController controller;

  @Override
  public void initialize(SupplySelectorController controller) {
    supplySelector.addSelectionListener(e -> lineContents.setObjects(e.getAllLineContents()));
    filter.getRenderer().setIconFunction(this::getIcon);
    filter.addActionListener(e -> controller.applyFilter(filter.getSelected().orElse(null)));
    filter.getModel().setAllowNullSelection(true);
    filter.getRenderer().setNoSelectionIcon(noFilterIcon);
    filter.getRenderer().setNoSelectionText("Alle Artikel");
    deleteSupply.setIcon(createIcon(FontAwesome.TRASH, new Color(0x5D1E01)));
    deleteSupply.addActionListener(controller::deleteCurrentSupply);
    export.setIcon(createIcon(FontAwesome.DOWNLOAD, new Color(0xDC7E00)));
    export.addActionListener(e -> controller.exportShoppingItems());
    printProduce.setIcon(produceIcon);
    printProduce.addActionListener(e -> controller.printProduce());
    viewOrders.setIcon(createIcon(FontAwesome.INFO_CIRCLE, new Color(0x3F2964)));
    viewOrders.addActionListener(controller::viewOrders);
    openArticle.setIcon(createIcon(FontAwesome.EYE, new Color(0x1A3186)));
    openArticle.addActionListener(e -> editArticle());
    mergeArticle.setIcon(createIcon(FontAwesome.FLOPPY_O, new Color(0x850010)));
    mergeArticle.addActionListener(e -> mergeArticle());
  }

  public void setLineContentFilter(Predicate<LineContent> filter) {
    lineContents.setRowFilter(filter::test);
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
            Columns.<Supply>create(
                    "Datum",
                    e -> Date.INSTANT_DATE_TIME.format(e.getDeliveryDate()),
                    SwingConstants.LEFT)
                .withSorter(Column.DATE_SORTER(Date.INSTANT_DATE_TIME)),
            Columns.create("Anzahl Artikel", Supply::getArticleCount)
                .withRightClickConsumer(this::listSupplyFiles),
            Columns.create("Summe", e -> String.format("%.2f€", e.getContentSum())));

    lineContents =
        new ObjectTable<>(
            Columns.<LineContent>createIconColumn("S", e -> getIcon(e.getStatus()))
                .withColumnAdjustor(
                    column -> column.setMaxWidth(Tools.scaleWithLabelScalingFactor(20))),
            Columns.create("Anz.", LineContent::getContainerMultiplier)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(50),
            Columns.create("VB", LineContent::getUserPreorderCount)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(40),
            Columns.create("Artikelnr.", LineContent::getKkNumber)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(80),
            Columns.create("Artikelname", LineContent::getName).withPreferredWidth(250),
            Columns.create("Hersteller", LineContent::getProducer).withPreferredWidth(100),
            Columns.create("E.preis", SupplySelectorController::formatDisplayPrice)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(100),
            Columns.<LineContent>create("Geb.", e -> Math.round(e.getContainerSize()))
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(40),
            Columns.<LineContent>create("Packung", LineContent::getContainerUnit)
                .withPreferredWidth(100)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Kommentar", LineContent::getMessage).withPreferredWidth(100),
            Columns.create("Preisliste", LineContent::getEstimatedPriceList)
                .withPreferredWidth(150),
            Columns.<LineContent>create(
                    "Aufschlaggr.", e -> e.getEstimatedSurchargeGroup().getNameWithSurcharge())
                .withPreferredWidth(150),
            Columns.<LineContent>create("Preis", e -> String.format("%.2f€", e.getTotalPrice()))
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Diff.", LineContent::getPriceDifference)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(40),
            Columns.<LineContent>create(
                    "Pfand",
                    e ->
                        String.format(
                            "%.2f€ / %.2f€", e.getSingleDeposit(), e.getContainerDeposit()))
                .withPreferredWidth(70),
            Columns.<LineContent>create("Ausw.", e -> e.isWeighableKb() ? "ja" : "nein")
                .withPreferredWidth(50)
                .withLeftClickConsumer(this::toggleWeighable),
            Columns.<LineContent>create(
                    "Warnhinweis", e -> getWarningMessage(e.getComparedToCatalog()))
                .withPreferredWidth(150),
            Columns.<LineContent>createIconColumn(
                    "OK", e -> e.isVerified() ? verified : notVerified)
                .withPreferredWidth(30)
                .withLeftClickConsumer(this::verifyLine));
  }

  private Object getWarningMessage(ArticleComparedToCatalogEntry compared) {
    if (compared == null) {
      return "";
    }
    String warning;
    switch (compared.getResultType()) {
      case BARCODE_CHANGED:
        warning = "geänderter Barcode!";
        break;
      case BARCODE_CONFLICT_SAME_SUPPLIER:
      case BARCODE_CONFLICT_OTHER_SUPPLIER:
        warning =
            String.format(
                "Barcode vorhanden (%s)!",
                compared.getConflictingArticle().getSupplier().getShortName());
        break;
      case NO_CATALOG_ENTRY:
        warning = "Barcode fehlt!";
        break;
      default:
        warning = "";
    }
    return warning;
  }

  private void toggleWeighable(LineContent lineContent) {
    controller.toggleWeighable(lineContent);
    try {
      lineContents.getModel().fireTableDataChanged();
    } catch (IllegalArgumentException ignored) {
    }
  }

  public void refreshTable() {
    int selectedRow = lineContents.getEditingRow();
    try {
      lineContents.getModel().fireTableDataChanged();
    } catch (IllegalArgumentException ignored) {
    }
    if (selectedRow > -1) {
      lineContents.selectRow(selectedRow);
    }
    lineContents.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
  }

  public void verifyLine(LineContent lineContent) {
    lineContent.verify(!lineContent.isVerified());
  }

  private void listSupplyFiles(Supply supply) {
    ObjectTable<SupplierFile> supplierFiles =
        new ObjectTable<>(Columns.create("Auftrag", e -> e.getHeader().getOrderNr()));
    supplierFiles.setObjects(supply.getSupplierFiles());
    new ComponentController(supplierFiles).openTab();
  }

  private static final Icon okIcon = createIcon(FontAwesome.CHECK_CIRCLE, new Color(0x239696));
  private static final Icon addedIcon = createIcon(FontAwesome.PLUS, new Color(0xB648BA));
  private static final Icon ignoreIcon =
      createIcon(FontAwesome.EXCLAMATION_CIRCLE, new Color(0xFF0000));
  private static final Icon produceIcon = createIcon(FontAwesome.LEAF, new Color(0x00BD1D));
  private static final Icon noProduceIcon = createIcon(FontAwesome.STAR, new Color(0xFFB700));
  private static final Icon noFilterIcon = createIcon(FontAwesome.QUESTION, Color.GRAY);

  private static final Icon verified = okIcon;
  private static final Icon notVerified = noFilterIcon;

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
      case NO_PRODUCE:
        return noProduceIcon;
      default:
        throw new IllegalArgumentException("Status has no icon: " + status);
    }
  }

  private void editArticle() {
    Optional<LineContent> selectedLine = lineContents.getSelectedObject();
    if (!selectedLine.isPresent()) {
      return;
    }
    ;
    controller.openArticle(selectedLine.get());
  }

  private void mergeArticle() {
    Optional<LineContent> selectedLine = lineContents.getSelectedObject();
    if (!selectedLine.isPresent()) {
      return;
    }
    ;
    controller.editArticle(selectedLine.get());
  }

  public Optional<Supply> getSelectedSupply() {
    return supplySelector.getSelectedObject();
  }

  public Optional<LineContent> getSelectedLineContent() {
    return lineContents.getSelectedObject();
  }

  public void messageConfirmDelete() {
    if (JOptionPane.showConfirmDialog(
            getContent(),
            "Willst du diese Lieferung wirklich löschen? Dieser Vorgang kann nicht rückgängig gemacht werden!",
            "Potenzieller Datenverlust",
            JOptionPane.YES_NO_OPTION)
        != JOptionPane.YES_OPTION) {
      throw new CancellationException();
    }
  }

  public boolean messageConfirmLineMerge() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Willst du die Daten dieser Zeile wirklich in den Artikelstamm übernehmen?",
            "Artikeldaten überschreiben",
            JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION;
  }

  public boolean messageConfirmArticleMerge() {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Willst du die Artikeldaten in den Lieferschein übernehmen?",
            "Lieferschein überschreiben",
            JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION;
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

  public boolean messageConfirmBarcode(LineContent lineContent, String barcode) {
    return JOptionPane.showConfirmDialog(
            getContent(),
            "Soll der Barcode \""
                + barcode
                + "\" für den Artikel \""
                + lineContent.getName()
                + "\" übernommen werden?",
            "Barcode übernehmen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            createIcon(FontAwesome.BARCODE, new Color(0)))
        == JOptionPane.YES_OPTION;
  }

  public void messageInvalidBarcode(String barcode) {
    message(
        "\"" + barcode + "\" ist kein gültiger Barcode.",
        "Ungültiger Barcode",
        JOptionPane.ERROR_MESSAGE);
  }

  // @spotless:off

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /** Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        main = new JPanel();
        main.setLayout(new GridLayoutManager(3, 2, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setAlignmentX(0.2f);
        main.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(600, -1), 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(300, -1), null, 0, false));
        scrollPane1.setViewportView(supplySelector);
        deleteSupply = new JButton();
        deleteSupply.setText("Lieferung löschen");
        panel1.add(deleteSupply, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loadingIndicator = new JPanel();
        loadingIndicator.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        loadingIndicator.setVisible(false);
        panel1.add(loadingIndicator, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Ordner wird gelesen...");
        loadingIndicator.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar1 = new JProgressBar();
        progressBar1.setIndeterminate(true);
        loadingIndicator.add(progressBar1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        viewOrders = new JButton();
        viewOrders.setText("Auftragsdetails");
        panel1.add(viewOrders, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setAlignmentX(1.0f);
        main.add(panel2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel2.add(scrollPane2, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setViewportView(lineContents);
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        filter = new AdvancedComboBox();
        panel2.add(filter, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        openArticle = new JButton();
        openArticle.setText("Artikel Öffnen");
        panel3.add(openArticle, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        printProduce = new JButton();
        printProduce.setText("Obst und Gemüse Verkaufspreise drucken");
        panel3.add(printProduce, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        export = new JButton();
        export.setText("Lieferung übernehmen");
        panel3.add(export, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mergeArticle = new JButton();
        mergeArticle.setText("Artikel Speichern und Öffnen");
        panel3.add(mergeArticle, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Filter:");
        panel2.add(label2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Artikel des Lieferscheins");
        main.add(label3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Lieferungen");
        main.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        main.add(spacer3, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

    // @spotless:on
}
