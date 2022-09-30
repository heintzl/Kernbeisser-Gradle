package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBEntities.Shelf;

public class InventoryShelfOverview extends Report {

  private final Collection<Shelf> shelves;
  private final LocalDate inventoryDate;

  public InventoryShelfOverview(Collection<Shelf> shelves, LocalDate inventoryDate) {
    super("inventoryShelfOverview", "Regalübersicht_" + inventoryDate.toString());
    this.inventoryDate = inventoryDate;
    this.shelves = shelves;
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("inventoryDate", inventoryDate);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return shelves;
  }
}
