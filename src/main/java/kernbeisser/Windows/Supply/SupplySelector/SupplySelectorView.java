package kernbeisser.Windows.Supply.SupplySelector;

import java.awt.Color;
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
  private JButton verifyArticlesButton;
  private JProgressBar progressBar1;
  private JPanel loadingIndicator;
  private JButton viewOrders;
  @Linked private SupplySelectorController controller;

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
    filter.getRenderer().setNoSelectionIcon(noFilterIcon);
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
              e.verify(true);
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
        new ObjectTable<>(
            Columns.<LineContent>createIconColumn("S", e -> getIcon(e.getStatus()))
                .withColumnAdjustor(
                    column -> column.setMaxWidth(Tools.scaleWithLabelScalingFactor(20))),
            Columns.create("Anz.", LineContent::getContainerMultiplier)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(50),
            Columns.create("Artikelnr.", LineContent::getKkNumber)
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(100),
            Columns.create("Artikelname", LineContent::getName).withPreferredWidth(250),
            Columns.<LineContent>create("E.preis", e -> String.format("%.2f€", e.getPrice()))
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(120),
            Columns.<LineContent>create("Geb.größe", e -> Math.round(e.getContainerSize()))
                .withSorter(Column.NUMBER_SORTER)
                .withPreferredWidth(80),
            Columns.<LineContent>create(
                    "Packung",
                    e -> e.getAmount() + (e.getUnit() == null ? "" : e.getUnit().getShortName()))
                .withPreferredWidth(120)
                .withSorter(Column.NUMBER_SORTER),
            Columns.create("Kommentar", LineContent::getMessage).withPreferredWidth(150),
            Columns.<LineContent>create("Preis", e -> String.format("%.2f€", e.getTotalPrice()))
                .withSorter(Column.NUMBER_SORTER),
            Columns.<LineContent>create("Ausw.", e -> e.isWeighable() ? "ja" : "nein")
                .withPreferredWidth(60),
            Columns.<LineContent>createIconColumn(
                    "geprüft", e -> e.isVerified() ? verified : notVerified)
                .withLeftClickConsumer(this::verifyLine));
    lineContents.addDoubleClickListener(controller::editArticle);
  }

  public void verifyLine(LineContent lineContent) {
    lineContent.verify(!lineContent.isVerified());
    lineContents.getModel().fireTableDataChanged();
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
  private static final Icon noFilterIcon = createIcon(FontAwesome.QUESTION, Color.RED);

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
}
