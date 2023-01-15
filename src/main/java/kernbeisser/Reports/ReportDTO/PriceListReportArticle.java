package kernbeisser.Reports.ReportDTO;

import java.time.Instant;
import java.util.Map;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.Useful.Date;
import lombok.Data;

@Data
public class PriceListReportArticle {

  private String name;
  private Double itemRetailPrice;
  private Integer kbNumber;
  private String suppliersShortName;
  private String shortBarcode;
  private Integer suppliersItemNumber;
  private String metricUnits;
  private Boolean weighAble;
  private String lastDeliveryMonth;
  private double containerSize;
  private String unitAmount;

  public static PriceListReportArticle ofArticle(
      Article article, Map<Integer, Instant> lastDeliveries) {
    PriceListReportArticle priceListArticle = new PriceListReportArticle();
    priceListArticle.name = article.getName();
    priceListArticle.itemRetailPrice = Articles.calculateArticleRetailPrice(article, 0, false);
    priceListArticle.kbNumber = article.getKbNumber();
    priceListArticle.suppliersShortName = article.getSupplier().getShortName();
    priceListArticle.suppliersItemNumber = article.getSuppliersItemNumber();
    priceListArticle.metricUnits = article.getMetricUnits().getName();
    priceListArticle.weighAble = article.isWeighable();
    priceListArticle.containerSize = article.getContainerSize();
    priceListArticle.unitAmount = Articles.getContentAmount(article);
    priceListArticle.lastDeliveryMonth =
        Date.INSTANT_MONTH_YEAR.format(lastDeliveries.get(article.getKbNumber()));
    return priceListArticle;
  }
}
