package kernbeisser.DBEntities.Types;

import java.time.Instant;
import java.time.LocalDate;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.ArticleStock;
import kernbeisser.DBEntities.Shelf;

public class ArticleStockField {
  public static FieldIdentifier<ArticleStock, Long> id =
      new FieldIdentifier<>(ArticleStock.class, "id");
  public static FieldIdentifier<ArticleStock, Article> article =
      new FieldIdentifier<>(ArticleStock.class, "article");
  public static FieldIdentifier<ArticleStock, Shelf> shelf =
      new FieldIdentifier<>(ArticleStock.class, "shelf");
  public static FieldIdentifier<ArticleStock, Double> counted =
      new FieldIdentifier<>(ArticleStock.class, "counted");
  public static FieldIdentifier<ArticleStock, LocalDate> inventoryDate =
      new FieldIdentifier<>(ArticleStock.class, "inventoryDate");
  public static FieldIdentifier<ArticleStock, Instant> createDate =
      new FieldIdentifier<>(ArticleStock.class, "createDate");
}
