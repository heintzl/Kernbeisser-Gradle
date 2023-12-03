package kernbeisser.Reports.ReportDTO;

import java.util.List;
import kernbeisser.DBEntities.Shelf;
import lombok.Data;

@Data
public class InventoryShelfStock {

  private final Shelf shelf;

  private final double netSum;

  private final double depositSum;

  public InventoryShelfStock(Shelf shelf, List<InventoryArticleStock> stocks) {
    this.shelf = shelf;
    double netSum = 0;
    double depositSum = 0;
    for (InventoryArticleStock stock : stocks) {
      if (stock.getShelf().equals(shelf)) {
        netSum += stock.getNetSum();
        depositSum += stock.getDepositSum();
      }
    }
    this.netSum = netSum;
    this.depositSum = depositSum;
  }
}
