package kernbeisser.Windows.Inventory;

import static javax.swing.SwingConstants.RIGHT;

import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormImplemetations.Shelf.ShelfController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Reports.*;
import kernbeisser.Security.Key;
import kernbeisser.Useful.CSV;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Inventory.Counting.CountingController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.Getter;
import lombok.SneakyThrows;

public class InventoryController extends Controller<InventoryView, InventoryModel> {
  @Linked @Getter private final ObjectViewController<Shelf> shelfViewController;

  @Key(PermissionKey.ACTION_OPEN_INVENTORY)
  public InventoryController() throws PermissionKeyRequiredException {
    super(new InventoryModel());
    this.shelfViewController =
        new ObjectViewController<Shelf>(
            "Regale",
            new ShelfController(),
            getModel()::searchShelf,
            false,
            Columns.create("Regal-Nr.", Shelf::getShelfNo)
                .withHorizontalAlignment(RIGHT)
                .withSorter(Column.NUMBER_SORTER)
                .withColumnAdjustor(
                    column -> {
                      column.setMaxWidth(100);
                      column.setPreferredWidth(100);
                    }),
            Columns.create("Beschreibung", Shelf::getLocation),
            Columns.create("Kommentar", Shelf::getComment),
            Columns.create(
                "Regal Preislisten",
                e ->
                    e.getPriceLists().stream()
                        .map(PriceList::toString)
                        .collect(Collectors.joining(", "))),
            Columns.<Shelf>create("Extra-Artikel", e -> e.getArticles().size())
                .withHorizontalAlignment(RIGHT)
                .withSorter(Column.NUMBER_SORTER)
                .withColumnAdjustor(
                    column -> {
                      column.setMaxWidth(140);
                      column.setPreferredWidth(140);
                    }),
            Columns.<Shelf>create("Summe", shelf -> String.format("%.2f", shelf.calculateTotal()))
                .withHorizontalAlignment(RIGHT)
                .withSorter(Column.NUMBER_SORTER)
                .withColumnAdjustor(
                    column -> {
                      column.setMaxWidth(120);
                      column.setPreferredWidth(120);
                    }));
  }

  @Override
  public void fillView(InventoryView inventoryView) {}

  public void openCountingWindow(ActionEvent actionEvent) {
    new CountingController().openTab();
  }

  public void print(InventoryReports selectedReport, boolean selected, boolean outputAsPdf) {
    List<Shelf> shelves;
    if (selected && InventoryReports.shelfSelectionAllowed().contains(selectedReport)) {
      shelves =
          new ArrayList<Shelf>(
              getShelfViewController().getSearchBoxController().getSelectedObjects());
    } else {
      shelves = Shelf.getAll();
    }
    shelves.sort(Comparator.comparingInt(Shelf::getShelfNo));
    Report report = null;
    LocalDate inventoryDate = Setting.INVENTORY_SCHEDULED_DATE.getDateValue();
    switch (selectedReport) {
      case SHELFOVERVIEW:
        report = new InventoryShelfOverview(shelves, inventoryDate);
        break;
      case SHELFDETAILS:
        report = new InventoryShelfDetails(shelves, inventoryDate);
        break;
      case COUNTINGLISTS:
        report = new InventoryCountingLists(shelves, inventoryDate);
        break;
      case INVENTORYRESULT:
        break;
      case INVENTORYSHELFRESULTS:
        report = new InventoryStocks(shelves, inventoryDate);
        break;
    }
    if (report == null) {
      return;
    }
    if (outputAsPdf) {
      report.exportPdf("Ausdruck läuft", Tools::showUnexpectedErrorWarning);
    } else {
      report.sendToPrinter("Ausdruck läuft", Tools::showUnexpectedErrorWarning);
    }
  }

  public void calculateInventory(ActionEvent actionEvent) {}

  @SneakyThrows
  public void exportShelves(File file) {
    CSV.dumpIntoCsv(shelfViewController.getSearchBoxController().getView().getObjectTable(), file);
  }

  public void changeInventoryDate(LocalDate date) {
    Setting.INVENTORY_SCHEDULED_DATE.changeValue(date);
  }
}
