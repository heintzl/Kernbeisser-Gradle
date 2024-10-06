package kernbeisser.Reports.ReportDTO;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Map;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Exeptions.InvalidValue;
import kernbeisser.Useful.Date;
import kernbeisser.Windows.Supply.SupplySelector.LineContent;
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
  private String containerDescription;
  private String producer;
  private String countryOfOrigin;
  private String identification;

  public static PriceListReportArticle ofArticle(
      Article article, Map<Integer, Instant> lastDeliveries) {
    PriceListReportArticle priceListArticle = new PriceListReportArticle();
    priceListArticle.name = article.getName();
    priceListArticle.itemRetailPrice =
        ArticleRepository.calculateArticleRetailPrice(article, 0, false);
    priceListArticle.kbNumber = article.getKbNumber();
    priceListArticle.suppliersShortName = article.getSupplier().getShortName();
    priceListArticle.suppliersItemNumber = article.getSuppliersItemNumber();
    priceListArticle.metricUnits = article.getMetricUnits().getName();
    priceListArticle.weighAble = article.isWeighable();
    priceListArticle.containerSize = article.getContainerSize();
    priceListArticle.unitAmount = getPriceInfoAmount(article);
    priceListArticle.shortBarcode = ArticleRepository.getShortBarcode(article);
    priceListArticle.lastDeliveryMonth =
        Date.INSTANT_MONTH_YEAR.format(
            lastDeliveries.getOrDefault(article.getKbNumber(), Instant.now()));
    return priceListArticle;
  }

  public static PriceListReportArticle ofProduceLineContent(LineContent lineContent)
      throws InvalidValue {
    DecimalFormat containerFormat = new DecimalFormat("#0.# x ");
    containerFormat.setRoundingMode(RoundingMode.HALF_UP);
    double multiplier = lineContent.getContainerMultiplier() * lineContent.getContainerSize();
    PriceListReportArticle priceListLineContent = new PriceListReportArticle();
    priceListLineContent.suppliersItemNumber = lineContent.getKkNumber();
    priceListLineContent.name = lineContent.getName();
    priceListLineContent.itemRetailPrice = lineContent.getProduceRetailPrice();
    String containerDescription = "";
    if (multiplier != 1.0) {
      containerDescription += containerFormat.format(multiplier);
    }
    containerDescription += lineContent.getContainerDescription();
    priceListLineContent.containerDescription = containerDescription;
    priceListLineContent.producer = lineContent.getProducer();
    priceListLineContent.countryOfOrigin = lineContent.getOrigin();
    priceListLineContent.identification = lineContent.getIdentification();
    return priceListLineContent;
  }

  public static String getPriceInfoAmount(Article a) {
    if (a.isWeighable()) {
      return "pro " + a.getMetricUnits().getDisplayUnit().getShortName();
    } else {
      return ArticleRepository.getPieceAmount(a);
    }
  }
}
