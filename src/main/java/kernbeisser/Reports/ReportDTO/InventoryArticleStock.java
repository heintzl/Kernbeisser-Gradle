package kernbeisser.Reports.ReportDTO;

import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Enums.MetricUnits;
import lombok.Cleanup;
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

  public InventoryArticleStock(Shelf shelf, ArticleStock stock) {
    this.shelf = shelf;
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
    this.netSum =
        count * article.getNetPrice() * (article.isWeighable() ? unit.getBaseFactor() : 1.0);
    this.depositSum = count * article.getSingleDeposit();
  }

  public static Stream<InventoryArticleStock> stockStreamOfShelf(Shelf shelf) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return shelf.getArticleStocks().stream().map(a -> new InventoryArticleStock(shelf, a));
  }
}
