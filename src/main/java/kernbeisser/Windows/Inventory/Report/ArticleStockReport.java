package kernbeisser.Windows.Inventory.Report;

import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class ArticleStockReport {
  String articleName;
  int kbNumber;
  double articleNetPrice;
  double articleCount;
  double articleTotalCountPrice;

  public static ArticleStockReport ofArticleStock(ArticleStock articleStock) {
    Article a = articleStock.getArticle();
    return new ArticleStockReport(
        a.getName(),
        a.getKbNumber(),
        a.getNetPrice(),
        articleStock.getCounted(),
        articleStock.calculateNetPrice());
  }
}
