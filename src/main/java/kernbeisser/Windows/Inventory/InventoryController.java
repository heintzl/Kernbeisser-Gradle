package kernbeisser.Windows.Inventory;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormImplemetations.Shelf.ShelfController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Useful.CSV;
import kernbeisser.Windows.Inventory.Counting.CountingController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import lombok.SneakyThrows;

public class InventoryController extends Controller<InventoryView, InventoryModel> {
  @Linked private final ObjectViewController<Shelf> shelfViewController;

  public InventoryController() throws PermissionKeyRequiredException {
    super(new InventoryModel());
    this.shelfViewController =
        new ObjectViewController<>(
            "Regale",
            new ShelfController(),
            getModel()::searchShelf,
            false,
            Columns.create("Regalposition", Shelf::getLocation),
            Columns.create("Regalkommentar", Shelf::getComment),
            Columns.create(
                "Regal Preislisten",
                e ->
                    e.getPriceLists().stream()
                        .map(PriceList::toString)
                        .collect(Collectors.joining(", "))),
            Columns.create("Summe", shelf -> String.format("%.2f", shelf.calculateTotal())));
  }

  @Override
  public void fillView(InventoryView inventoryView) {}

  public void openCountingWindow(ActionEvent actionEvent) {
    new CountingController().openTab();
  }

  public void printCountingLists(ActionEvent actionEvent) {}

  public void calculateInventory(ActionEvent actionEvent) {}

  @SneakyThrows
  public void exportShelves(File file) {
    CSV.dumpIntoCsv(shelfViewController.getSearchBoxController().getView().getObjectTable(), file);
  }
}
