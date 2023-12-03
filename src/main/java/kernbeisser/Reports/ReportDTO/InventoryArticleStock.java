package kernbeisser.Reports.ReportDTO;

import java.util.List;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.FieldCondition;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.Setting;
import lombok.Data;

@Data
public class InventoryArticleStock {

  private final Shelf shelf;
  private final Article article;
  private final String amount;
  private final double netSum;
  private final double depositSum;
  private final String unit;
  private final double count;

  public InventoryArticleStock(ArticleStock stock) {
    this.shelf = stock.getShelf();
    this.article = stock.getArticleAtInventoryDate();
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
    this.netSum = stock.calculateNetPrice();
    this.depositSum = count * article.getSingleDeposit();
  }

  public static List<InventoryArticleStock> getStocks() {
    return DBConnection.getConditioned(
            ArticleStock.class,
            new FieldCondition("inventoryDate", Setting.INVENTORY_SCHEDULED_DATE.getDateValue()))
        .stream()
        .filter(s -> s.getCounted() != 0.0)
        .map(InventoryArticleStock::new)
        .collect(Collectors.toList());
  }
}
