package kernbeisser.Windows.Inventory.Report;

import java.util.Collection;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import kernbeisser.DBEntities.Shelf;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class ShelfReportDTO {
  String shelfLocation;
  String shelfComment;
  Collection<ArticleStockReport> articleStocks;
  double shelfSum;

  public static ShelfReportDTO ofShelf(EntityManager em, Shelf shelf) {
    Collection<ArticleStockReport> articleStockDTOS =
        shelf
            .getAllArticleStocks(em)
            .map(ArticleStockReport::ofArticleStock)
            .collect(Collectors.toList());
    return new ShelfReportDTO(
        shelf.getLocation(),
        shelf.getComment(),
        articleStockDTOS,
        articleStockDTOS.stream().mapToDouble(ArticleStockReport::getArticleTotalCountPrice).sum());
  }
}
