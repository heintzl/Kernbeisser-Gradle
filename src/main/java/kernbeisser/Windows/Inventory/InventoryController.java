package kernbeisser.Windows.Inventory;

import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormImplemetations.Shelf.ShelfController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Windows.MVC.Controller;

public class InventoryController extends Controller<InventoryView, InventoryModel> {
  private final ObjectViewController<Shelf> shelfViewController;

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
                        .collect(Collectors.joining(", "))));
  }

  @Override
  public void fillView(InventoryView inventoryView) {}
}
