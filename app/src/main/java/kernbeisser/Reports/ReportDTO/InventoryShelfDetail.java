package kernbeisser.Reports.ReportDTO;

import java.util.stream.Stream;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Shelf;
import lombok.Data;

@Data
public class InventoryShelfDetail {

  private final Shelf shelf;

  private final PriceList priceList;

  private final int extraArticles;

  public InventoryShelfDetail(Shelf shelf, PriceList priceList) {
    this.shelf = shelf;
    this.priceList = priceList;
    this.extraArticles = shelf.getArticles().size();
  }

  public static Stream<InventoryShelfDetail> detailsOfShelf(Shelf shelf) {
    return shelf.getPriceLists().stream().map(p -> new InventoryShelfDetail(shelf, p));
  }
}
