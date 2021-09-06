package kernbeisser.Windows.Supply.SupplySelector;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IView;
import org.jetbrains.annotations.NotNull;

public class SupplySelectorView implements IView<SupplySelectorController> {

  private JPanel main;
  private ObjectTable<Supply> supplySelector;
  private JButton deleteSupply;
  private JButton openOtherFolder;
  private JButton export;
  private ObjectTable<LineContent> lineContents;
  private AdvancedComboBox<ResolveStatus> filter;
  private JButton printProduce;

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
    openOtherFolder.addActionListener(e -> controller.requestDirectoryChange());
    export.addActionListener(e -> controller.exportShoppingItems());
    printProduce.addActionListener(e -> controller.printProduce());
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
            Columns.create("Anzahl Artikel", Supply::getArticleCount),
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
            Columns.createIconColumn("S", e -> getIcon(e.getStatus())));
  }

  private static final Icon okIcon = createIcon(FontAwesome.CHECK_CIRCLE, new Color(0x238678));
  private static final Icon addedIcon = createIcon(FontAwesome.PLUS, new Color(0xB648BA));
  private static final Icon ignoreIcon = createIcon(FontAwesome.EXCLAMATION_CIRCLE, Color.ORANGE);
  private static final Icon produceIcon = createIcon(FontAwesome.LEAF, new Color(0x0D5C0A));

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

  public void messageSelectSupplyFirst() {
    message("Bitte wähle zunächst eine Lieferung aus!", "Keine Lieferung ausgewählt!");
  }

}
