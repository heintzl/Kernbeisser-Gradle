package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Reports.ReportDTO.InventoryShelfDetail;

public class InventoryShelfDetails extends Report {
  private final Collection<Shelf> shelves;
  private final LocalDate inventoryDate;

  public InventoryShelfDetails(Collection<Shelf> shelves, LocalDate inventoryDate) {
    super(ReportFileNames.INVENTORY_SHELF_DETAILS_REPORT_FILENAME);
    this.inventoryDate = inventoryDate;
    this.shelves = shelves;
  }

  @Override
  String createOutFileName() {
    return "Regaldetails_" + inventoryDate.toString();
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("inventoryDate", inventoryDate);
    return params;
  }

  @Override
  Collection<InventoryShelfDetail> getDetailCollection() {
    return shelves.stream()
        .flatMap(InventoryShelfDetail::detailsOfShelf)
        .collect(Collectors.toList());
  }
}
