package kernbeisser.Reports;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.Reports.ReportDTO.PriceListReportArticle;

public class PriceListReport extends Report {
  private final Collection<PriceListReportArticle> priceListReportArticles;
  private final String priceListName;

  public PriceListReport(PriceList priceList) {
    this(priceList.getAllArticles(), priceList.getName());
  }

  public PriceListReport(Collection<Article> articles, String priceListName) {
    super(ReportFileNames.PRICELIST_REPORT_FILENAME);
    setDuplexPrint(false);
    Map<Integer, Instant> lastDeliveries = Articles.getLastDeliveries();
    this.priceListReportArticles =
        articles.stream()
            .map(a -> PriceListReportArticle.ofArticle(a, lastDeliveries))
            .collect(Collectors.toList());
    this.priceListName = priceListName;
  }

  @Override
  String createOutFileName() {
    return "Preisliste " + priceListName;
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
