package kernbeisser.Windows.Inventory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.Instant;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import jiconfont.IconCode;
import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.DBEntities.Shelf;
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
    JButton printPriceListCountingList = new JButton("Zähllisten drucken");
    printPriceListCountingList.addActionListener(controller::printCountingLists);
    printPriceListCountingList.setIcon(Icons.defaultIcon(FontAwesome.PRINT, new Color(0x068DE1)));
    JButton exportShelves = new JButton("Regale exportieren");
    exportShelves.addActionListener(this::exportShelves);
    exportShelves.setIcon(Icons.defaultIcon(FontAwesome.DOWNLOAD, new Color(0x00A201)));
    JButton shelfCounting = new JButton("Zähllisten eingeben");
    shelfCounting.addActionListener(controller::openCountingWindow);
    shelfCounting.setIcon(Icons.defaultIcon(FontAwesome.LIST, new Color(0x01FF78)));
    JButton printResults = new JButton("Inventur Ergebnisse drucken");
    printResults.addActionListener(controller::calculateInventory);
    printResults.setIcon(Icons.defaultIcon(FontAwesome.CALCULATOR, new Color(0xC71BC5)));
    shelfViewController.addButton(exportShelves);
    shelfViewController.addButton(printPriceListCountingList);
    shelfViewController.addButton(shelfCounting);
    shelfViewController.addButton(printResults);
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
