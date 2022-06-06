package kernbeisser.Reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Reports.ReportDTO.PriceListReportArticle;

public class PriceListReport extends Report {

  private final Collection<PriceListReportArticle> priceListReportArticles;
  private final String priceListName;

  public PriceListReport(PriceList priceList) {
    this(
        priceList.getAllArticles().stream()
            .map(ShoppingItem::createReportItem)
            .collect(Collectors.toList()),
        priceList.getName());
  }

  public PriceListReport(
      Collection<PriceListReportArticle> priceListReportArticles, String priceListName) {
    super("priceList", "Preisliste " + priceListName);
    setDuplexPrint(false);
    this.priceListReportArticles = priceListReportArticles;
    this.priceListName = priceListName;
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("priceList", priceListName);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return priceListReportArticles;
  }
}
