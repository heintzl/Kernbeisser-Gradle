package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;
import java.time.LocalDate;

public class ArticleStockField {
public static FieldIdentifier<ArticleStock,Long> id = new FieldIdentifier<>(ArticleStock.class, Long.class, "id");
public static FieldIdentifier<ArticleStock,Article> article = new FieldIdentifier<>(ArticleStock.class, Article.class, "article");
public static FieldIdentifier<ArticleStock,Shelf> shelf = new FieldIdentifier<>(ArticleStock.class, Shelf.class, "shelf");
public static FieldIdentifier<ArticleStock,Double> counted = new FieldIdentifier<>(ArticleStock.class, Double.class, "counted");
public static FieldIdentifier<ArticleStock, LocalDate> inventoryDate = new FieldIdentifier<>(ArticleStock.class, LocalDate.class, "inventoryDate");
public static FieldIdentifier<ArticleStock,Instant> createDate = new FieldIdentifier<>(ArticleStock.class, Instant.class, "createDate");

}