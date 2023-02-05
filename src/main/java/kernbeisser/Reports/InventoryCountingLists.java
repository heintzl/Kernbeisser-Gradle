package kernbeisser.Reports;

import kernbeisser.DBEntities.Shelf;
import kernbeisser.Reports.ReportDTO.InventoryArticle;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryCountingLists extends Report {
  private final Collection<Shelf> shelves;
  private final LocalDate inventoryDate;

  public InventoryCountingLists(Collection<Shelf> shelves, LocalDate inventoryDate) {
    super(ReportFileNames.INVENTORY_COUNTING_LISTS_REPORT_FILENAME);
    this.inventoryDate = inventoryDate;
    setDuplexPrint(false);
    this.shelves = shelves;
  }

  @Override
  String createOutFileName() {
    return "Zähllisten_" + inventoryDate.toString();
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
            .flatMap(InventoryArticle::articleStreamOfShelf)
        .collect(Collectors.toList());
  }
}
