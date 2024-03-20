package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class ArticleStockField {
public static FieldIdentifier<kernbeisser.DBEntities.ArticleStock,Long> id = new FieldIdentifier<>(kernbeisser.DBEntities.ArticleStock.class, Long.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.ArticleStock,kernbeisser.DBEntities.Article> article = new FieldIdentifier<>(kernbeisser.DBEntities.ArticleStock.class, kernbeisser.DBEntities.Article.class, "article");
public static FieldIdentifier<kernbeisser.DBEntities.ArticleStock,kernbeisser.DBEntities.Shelf> shelf = new FieldIdentifier<>(kernbeisser.DBEntities.ArticleStock.class, kernbeisser.DBEntities.Shelf.class, "shelf");
public static FieldIdentifier<kernbeisser.DBEntities.ArticleStock,Double> counted = new FieldIdentifier<>(kernbeisser.DBEntities.ArticleStock.class, Double.class, "counted");
public static FieldIdentifier<kernbeisser.DBEntities.ArticleStock,java.time.LocalDate> inventoryDate = new FieldIdentifier<>(kernbeisser.DBEntities.ArticleStock.class, java.time.LocalDate.class, "inventoryDate");
public static FieldIdentifier<kernbeisser.DBEntities.ArticleStock,java.time.Instant> createDate = new FieldIdentifier<>(kernbeisser.DBEntities.ArticleStock.class, java.time.Instant.class, "createDate");

}