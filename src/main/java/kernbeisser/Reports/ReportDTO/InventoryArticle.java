package kernbeisser.Reports.ReportDTO;

import java.util.stream.Stream;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Articles;
import kernbeisser.DBEntities.Shelf;
import lombok.Data;

@Data
public class InventoryArticle {

  private final Shelf shelf;
  private final String name;
  private final double netPrice;
  private final int kbNumber;
  private final String amount;
  private final int count;
  private final boolean weighable;

  public InventoryArticle(Shelf shelf, Article article, int count) {
    this.shelf = shelf;
    this.name = article.getName();
    this.netPrice = article.getNetPrice();
    this.kbNumber = article.getKbNumber();
    this.amount = Articles.getPieceAmount(article);
    this.weighable = article.isWeighable();
    this.count = count;
  }

  public static Stream<InventoryArticle> articleStreamOfShelf(Shelf shelf) {
    int count = shelf.getAllArticles().size();
    return shelf.getAllArticles().stream().map(a -> new InventoryArticle(shelf, a, count));
  }
}
