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
    JButton shelfCounting = new JButton("Zähllisten eingeben");
    shelfCounting.addActionListener(controller::openCountingWindow);
    shelfCounting.setIcon(Icons.defaultIcon(FontAwesome.LIST, new Color(0x01FF78)));
    JButton print = new JButton("Inventur Ergebnisse drucken");
    print.addActionListener(e -> print());
    print.setIcon(Icons.defaultIcon(FontAwesome.PRINT, new Color(0x02277E)));
    shelfViewController.addButton(shelfCounting);
    shelfViewController.addButton(exportShelves);
    shelfViewController.addButton(print);
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

  private void print() {
    boolean selectedShelves =
        controller.getShelfViewController().getSearchBoxController().getSelectedObjects().size()
            > 0;
    JPanel printOptions = new JPanel();
    printOptions.setLayout(new GridLayout(0, 1));
    JCheckBox confirmSelected = new JCheckBox("Ausdruck auf die ausgewählten Listen beschränken");
    confirmSelected.setVisible(selectedShelves);
    JComboBox<InventoryReports> report = new JComboBox<>(InventoryReports.values());
    Supplier<Boolean> shelfSelectionCurrentlyAllowed =
        (() ->
            InventoryReports.shelfSelectionAllowed()
                .contains((InventoryReports) report.getSelectedItem()));
    report.addActionListener(
        e -> confirmSelected.setEnabled(selectedShelves && shelfSelectionCurrentlyAllowed.get()));
    JLabel reportLabel = new JLabel("Ausdruck auswählen:");
    reportLabel.setLabelFor(report);
    JCheckBox outputAsPdf = new JCheckBox("PDF als Vorschau erstellen");
    confirmSelected.setEnabled(shelfSelectionCurrentlyAllowed.get());

    printOptions.add(reportLabel);
    printOptions.add(report);
    printOptions.add(outputAsPdf);
    printOptions.add(confirmSelected);
    printOptions.setAlignmentX(Component.LEFT_ALIGNMENT);

    if (JOptionPane.showConfirmDialog(
            getContent(),
            printOptions,
            "Ausdruck starten",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            IconFontSwing.buildIcon(FontAwesome.PRINT, 40, new Color(0x02277E)))
        == JOptionPane.OK_OPTION) {
      controller.print(
          (InventoryReports) report.getSelectedItem(),
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
