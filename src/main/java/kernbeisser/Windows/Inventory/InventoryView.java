package kernbeisser.Windows.Inventory;

import com.github.lgooddatepicker.components.DatePicker;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Instant;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
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
    shelfViewController.addButton(shelfCounting);
    shelfViewController.addButton(print);
    shelfViewController.addButton(exportShelves);
    shelfViewController.setForceExtraButtonState(false);
    shelfViewController.setExtraButtonsAvailable(true);
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

  private void setCheckboxState(JCheckBox checkBox, boolean enable) {
    checkBox.setEnabled(enable);
    if (!enable) {
      checkBox.setSelected(false);
    }
  }

  private void print() {
    boolean selectedShelves =
        controller.getShelfViewController().getSearchBoxController().getSelectedObjects().size()
            > 0;
    JPanel printOptions = new JPanel();
    printOptions.setLayout(new GridLayout(0, 1));
    JCheckBox confirmSelected = new JCheckBox("Ausdruck auf die ausgewählten Regale beschränken");
    if (selectedShelves) {
      setCheckboxState(confirmSelected, true);
    } else {
      confirmSelected.setVisible(false);
    }
    JComboBox<InventoryReports> reportSelector = new JComboBox<>(InventoryReports.values());
    Supplier<Boolean> shelfSelectionCurrentlyAllowed =
        (() ->
            InventoryReports.shelfSelectionAllowed()
                .contains((InventoryReports) reportSelector.getSelectedItem()));
    reportSelector.addActionListener(
        e ->
            setCheckboxState(
                confirmSelected, selectedShelves && shelfSelectionCurrentlyAllowed.get()));
    JLabel reportLabel = new JLabel("Ausdruck auswählen:");
    reportLabel.setLabelFor(reportSelector);
    JCheckBox outputAsPdf = new JCheckBox("PDF als Vorschau erstellen");
    confirmSelected.setEnabled(shelfSelectionCurrentlyAllowed.get());

    printOptions.add(reportLabel);
    printOptions.add(reportSelector);
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
      controller.print(
          (InventoryReports) reportSelector.getSelectedItem(),
          confirmSelected.isSelected(),
          outputAsPdf.isSelected());
    }
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
