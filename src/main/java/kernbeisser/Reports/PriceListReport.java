package kernbeisser.Reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.ShoppingItem;

public class PriceListReport extends Report {

  private final PriceList priceList;

  public PriceListReport(PriceList priceList) {
    super("priceList", "Preisliste " + priceList.getName());
    this.priceList = priceList;
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("priceList", priceList.getName());
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return priceList.getAllArticles().stream()
        .map(ShoppingItem::createReportItem)
        .collect(Collectors.toList());
  }
}
