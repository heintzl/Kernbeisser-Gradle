package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Reports.ReportDTO.InventoryArticleStock;
import kernbeisser.Reports.ReportDTO.InventoryShelfStock;

public class InventoryStocks extends Report {

  private final LocalDate inventoryDate;
  private final List<InventoryArticleStock> stocks;

  public InventoryStocks(LocalDate inventoryDate) {
    super(ReportFileNames.INVENTORY_STOCKS);
    this.inventoryDate = inventoryDate;
    stocks = InventoryArticleStock.getStocks();
    setDuplexPrint(false);
  }

  @Override
  String createOutFileName() {
    return "InventurBestände_" + inventoryDate.toString();
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("inventoryDate", inventoryDate);
    double netSum = 0.0;
    double depositSum = 0.0;
    for (InventoryArticleStock s : stocks) {
      netSum += s.getNetSum();
      depositSum += s.getDepositSum();
    }
    params.put("netTotal", netSum);
    params.put("depositTotal", depositSum);
    params.put("total", netSum + depositSum);
    return params;
  }

  @Override
  Collection<InventoryShelfStock> getDetailCollection() {
    return Shelf.getAll().stream()
        .map(s -> new InventoryShelfStock(s, stocks))
        .collect(Collectors.toList());
  }
}
