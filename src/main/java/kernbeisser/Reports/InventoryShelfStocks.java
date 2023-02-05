package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Reports.ReportDTO.InventoryArticleStock;

public class InventoryShelfStocks extends Report {

  private final Collection<Shelf> shelves;
  private final LocalDate inventoryDate;

  public InventoryShelfStocks(Collection<Shelf> shelves, LocalDate inventoryDate) {
    super(ReportFileNames.INVENTORY_SHELF_STOCKS);
    this.inventoryDate = inventoryDate;
    setDuplexPrint(false);
    this.shelves = shelves;
  }

  @Override
  String createOutFileName() {
    return "InventurRegalBestände_" + inventoryDate.toString();
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("inventoryDate", inventoryDate);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return shelves.stream()
        .flatMap(InventoryArticleStock::stockStreamOfShelf)
        .filter(s -> s.getCount() != 0.0)
        .collect(Collectors.toList());
  }
}
