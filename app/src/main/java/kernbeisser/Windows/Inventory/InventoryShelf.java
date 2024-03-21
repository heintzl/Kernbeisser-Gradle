package kernbeisser.Windows.Inventory;

import com.google.common.collect.Maps;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.DBEntities.TypeFields.ArticleStockField;
import kernbeisser.Enums.Setting;
import kernbeisser.Useful.Tools;
import lombok.Getter;

@Getter
public class InventoryShelf {

  private final Shelf shelf;
  private double netValue;
  private double depositValue;

  public InventoryShelf(Shelf shelf) {
    this.shelf = shelf;
    updateValues();
  }

  private void updateValues() {

    LocalDate localDate = Setting.INVENTORY_SCHEDULED_DATE.getDateValue();
    List<ArticleStock> shelfStocks =
        DBConnection.getConditioned(
            ArticleStock.class,
            ArticleStockField.shelf.eq(shelf),
            ArticleStockField.inventoryDate.eq(localDate));

    List<Integer> stockArticleIds =
        shelfStocks.stream().map(s -> s.getArticle().getId()).collect(Collectors.toList());
    Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    Map<Integer, Article> indexedArticlesAtDate =
        Maps.uniqueIndex(
            ArticleRepository.getArticlesStateAtDate(date, stockArticleIds), Article::getId);
    double net = 0;
    double deposit = 0;

    for (ArticleStock stock : shelfStocks) {
      if (stock.getCounted() > 0.0) {
        Article historicalArticle =
            Tools.ifNull(indexedArticlesAtDate.get(stock.getArticle().getId()), stock.getArticle());
        net +=
            historicalArticle.getNetPrice()
                * stock.getCounted()
                * (historicalArticle.isWeighable()
                    ? historicalArticle.getMetricUnits().getBaseFactor()
                    : 1.0);
        deposit += stock.getCounted() * historicalArticle.getSingleDeposit();
      }
    }
    netValue = net;
    depositValue = deposit;
  }
}
