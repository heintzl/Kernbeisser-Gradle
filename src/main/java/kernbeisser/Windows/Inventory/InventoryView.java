package kernbeisser.Windows.Inventory;

import com.github.lgooddatepicker.components.DatePicker;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Instant;
import java.util.Set;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.CustomComponents.ObjectTable.ObjectTable;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.Setting;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Forms.ObjectView.ObjectViewView;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Icons;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class InventoryView implements IView<InventoryController> {
  private JPanel main;
  private ObjectViewView<Shelf> shelfView;
  private final int printIconColor = 0x0033AF;
  private InventoryReports selectedReport;
  private boolean pdfOutput = false;
  private boolean printOnlySelected = false;

  @Linked private ObjectViewController<Shelf> shelfViewController;
  @Linked private InventoryController controller;

  @Override
  public void initialize(InventoryController controller) {
    JLabel dateLabel = new JLabel("Inventurdatum:");
    DatePicker datePicker = new DatePicker();
    datePicker.setAlignmentX(JLabel.LEFT_ALIGNMENT);
    datePicker.setDate(Setting.INVENTORY_SCHEDULED_DATE.getDateValue());
    datePicker.addDateChangeListener(e -> controller.changeInventoryDate(datePicker.getDate()));
    shelfViewController.addComponents(dateLabel, datePicker);

    JButton exportShelves = new JButton("Regale exportieren");
    exportShelves.addActionListener(this::exportShelves);
    exportShelves.setIcon(Icons.defaultIcon(FontAwesome.DOWNLOAD, new Color(0x00A201)));
    JButton shelfCounting = new JButton("Zählergebnisse eingeben");
    shelfCounting.addActionListener(
        e ->
            controller.openCountingWindow(
                shelfViewController.getSearchBoxController().getSelectedObject().orElse(null)));
    shelfCounting.setIcon(Icons.defaultIcon(FontAwesome.LIST, new Color(0x01FF78)));
    JButton print = new JButton("Listen und Ergebnisse drucken");
    print.addActionListener(e -> print());
    print.setIcon(Icons.defaultIcon(FontAwesome.PRINT, new Color(printIconColor)));
    JButton shelfLessPriceLists = new JButton("Preislisten ohne Regal");
    shelfLessPriceLists.addActionListener(e -> controller.showPriceListsWithoutShelf());
    shelfLessPriceLists.setIcon(Icons.defaultIcon(FontAwesome.BARS, new Color(0xDC7E00)));
    shelfViewController.addButton(shelfCounting);
    shelfViewController.addButton(shelfLessPriceLists);
    shelfViewController.addButton(print);
    shelfViewController.addButton(exportShelves);
    shelfViewController.setForceExtraButtonState(false);
    shelfViewController.setExtraButtonsAvailable(true);
    selectedReport = InventoryReports.values()[0];
  }

  private void exportShelves(ActionEvent actionEvent) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Regal csv Datei exportieren");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV-Datei", "csv"));
    fileChooser.setSelectedFile(
        new File("Regale" + Date.INSTANT_DATE.format(Instant.now()) + ".csv"));
    if (fileChooser.showSaveDialog(getTopComponent()) == JFileChooser.APPROVE_OPTION) {
      controller.exportShelves(fileChooser.getSelectedFile());
    }
  }

  private void setConfirmCheckboxState(JCheckBox checkBox, boolean enable) {
    checkBox.setEnabled(enable);
    checkBox.setSelected(enable && printOnlySelected);
  }

  private void print() {
    boolean selectedShelves =
        controller.getShelfViewController().getSearchBoxController().getSelectedObjects().size()
            > 0;
    JPanel printOptions = new JPanel();
    printOptions.setLayout(new GridLayout(0, 1));
    JCheckBox confirmSelected = new JCheckBox("Ausdruck auf die ausgewählten Regale beschränken");
    if (!selectedShelves) {
      confirmSelected.setVisible(false);
    }
    Supplier<Boolean> shelfSelectionCurrentlyAllowed =
        (() -> InventoryReports.shelfSelectionAllowed().contains(selectedReport));
    ButtonGroup optReports = new ButtonGroup();
    JLabel reportLabel = new JLabel("Ausdruck auswählen:");
    JCheckBox outputAsPdf = new JCheckBox("PDF als Vorschau erstellen");
    outputAsPdf.setSelected(pdfOutput);
    outputAsPdf.addActionListener(e -> pdfOutput = outputAsPdf.isSelected());
    setConfirmCheckboxState(
        confirmSelected, selectedShelves && shelfSelectionCurrentlyAllowed.get());
    confirmSelected.addActionListener(e -> printOnlySelected = confirmSelected.isSelected());

    printOptions.add(reportLabel);
    for (InventoryReports report : InventoryReports.values()) {
      JRadioButton button = new JRadioButton(report.toString());
      button.setSelected(report.equals(selectedReport));
      button.addActionListener(
          e -> {
            selectedReport = report;
            setConfirmCheckboxState(
                confirmSelected, selectedShelves && shelfSelectionCurrentlyAllowed.get());
          });
      printOptions.add(button);
      optReports.add(button);
    }
    printOptions.add(new JLabel(""));
    printOptions.add(outputAsPdf);
    printOptions.add(confirmSelected);
    printOptions.setAlignmentX(Component.LEFT_ALIGNMENT);

    if (JOptionPane.showConfirmDialog(
            getContent(),
            printOptions,
            "Ausdruck starten",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            IconFontSwing.buildIcon(FontAwesome.PRINT, 40, new Color(printIconColor)))
        == JOptionPane.OK_OPTION) {
      controller.print(selectedReport, confirmSelected.isSelected(), outputAsPdf.isSelected());
    }
  }

  public void showPriceListsWithoutShelf(Set<PriceList> priceLists) {
    ObjectTable<PriceList> priceListTable =
        new ObjectTable(
            Columns.create("Preisliste", PriceList::getName)
                .withColumnAdjustor(column -> column.setPreferredWidth(550)),
            Columns.<PriceList>create(
                    "Artikel", p -> String.format("%d", p.getAllArticles().size()))
                .withSorter(Column.NUMBER_SORTER));
    JPanel tablePanel = new JPanel(new FlowLayout());
    tablePanel.setSize(700, 1000);
    JScrollPane scrollPane =
        new JScrollPane(
            priceListTable,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    tablePanel.add(scrollPane);
    priceListTable.setObjects(priceLists);
    JOptionPane.showMessageDialog(
        getContent(), tablePanel, "Nicht zugeordnete Preislisten", JOptionPane.PLAIN_MESSAGE);
  }

  public boolean confirmPrint() {
    return JOptionPane.showConfirmDialog(getContent(), "wirklich", "Abfrage", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    shelfView = shelfViewController.getView();
  }

  @Override
  public String getTitle() {
    return "Inventur";
  }

  @StaticAccessPoint
  @Override
  public IconCode getTabIcon() {
    return FontAwesome.CALCULATOR;
  }
}
