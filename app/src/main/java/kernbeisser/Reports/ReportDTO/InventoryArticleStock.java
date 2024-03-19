package kernbeisser.Reports.ReportDTO;

import com.google.common.collect.Maps;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.DBEntities.Types.ArticleStockField;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import lombok.Data;

@Data
public class InventoryArticleStock {

  private final Shelf shelf;
  private Article article;
  private final String amount;
  private double netSum;
  private double depositSum;
  private final String unit;
  private final double count;

  public InventoryArticleStock(ArticleStock stock) {
    this.shelf = stock.getShelf();
    this.article = stock.getArticle();
    this.amount = Articles.getPieceAmount(article);
    MetricUnits unit = article.getMetricUnits();
    if (article.isWeighable()) {
      switch (unit) {
        case MILLILITER:
        case LITER:
          this.unit = "ml";
          break;
        case KILOGRAM:
        case GRAM:
        case MILLIGRAM:
          this.unit = "g";
          break;
        default:
          this.unit = "Stk";
      }
    } else {
      this.unit = "Stk";
    }
    this.count = stock.getCounted();
  }

  private static List<InventoryArticleStock> getStocksAtDate(
      LocalDate localDate, List<InventoryArticleStock> stocks) {
    List<Integer> stockArticleIds =
        stocks.stream().map(s -> s.getArticle().getId()).collect(Collectors.toList());
    Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    Map<Integer, Article> indexedArticlesAtDate =
        Maps.uniqueIndex(Articles.getArticlesStateAtDate(date, stockArticleIds), Article::getId);
    return stocks.stream()
        .map(
            s ->
                s.setHistoricalArticle(
                    Tools.ifNull(
                        indexedArticlesAtDate.get(s.getArticle().getId()), s.getArticle())))
        .collect(Collectors.toList());
  }

  private InventoryArticleStock setHistoricalArticle(Article historicalArticle) {
    this.article = historicalArticle;
    this.netSum =
        historicalArticle.getNetPrice()
            * count
            * (historicalArticle.isWeighable()
                ? historicalArticle.getMetricUnits().getBaseFactor()
                : 1.0);
    this.depositSum = count * historicalArticle.getSingleDeposit();
    return this;
  }

  public static List<InventoryArticleStock> getStocks() {
    LocalDate currentInventoryDate = Setting.INVENTORY_SCHEDULED_DATE.getDateValue();
    List<InventoryArticleStock> stocksWithoutSums =
        DBConnection.getConditioned(
                ArticleStock.class, ArticleStockField.inventoryDate.eq(currentInventoryDate))
            .stream()
            .filter(s -> s.getCounted() != 0.0)
            .map(InventoryArticleStock::new)
            .sorted(
                Comparator.comparingInt(
                    e -> e.getShelf().getShelfNo() * 1000000 + e.getArticle().getKbNumber()))
            .collect(Collectors.toList());
    return getStocksAtDate(currentInventoryDate, stocksWithoutSums);
  }
}
