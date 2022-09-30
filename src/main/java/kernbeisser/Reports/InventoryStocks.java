package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Reports.ReportDTO.InventoryArticleStock;

public class InventoryStocks extends Report {

  private final Collection<Shelf> shelves;
  private final LocalDate inventoryDate;

  public InventoryStocks(Collection<Shelf> shelves, LocalDate inventoryDate) {
    super("inventoryStock", "InventurBestände_" + inventoryDate.toString());
    this.inventoryDate = inventoryDate;
    setDuplexPrint(false);
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
    return shelves.stream()
        .flatMap(InventoryArticleStock::stockStreamOfShelf)
        .collect(Collectors.toList());
  }
}
