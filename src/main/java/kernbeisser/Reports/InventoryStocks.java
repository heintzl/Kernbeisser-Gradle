package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Shelf;

public class InventoryStocks extends Report {

  private final LocalDate inventoryDate;

  public InventoryStocks(LocalDate inventoryDate) {
    super("inventoryStock", "InventurBestände_" + inventoryDate.toString());
    this.inventoryDate = inventoryDate;
    setDuplexPrint(false);
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("inventoryDate", inventoryDate);
    double netSum = 0.0;
    double depositSum = 0.0;
    for (ArticleStock s : ArticleStock.getAllCurrentStocks()) {
      netSum += s.calculateNetPrice();
      depositSum += s.calculateDeposit();
    }
    params.put("netSum", netSum);
    params.put("depositSum", depositSum);
    params.put("sum", netSum + depositSum);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return Shelf.getAll();
  }
}
