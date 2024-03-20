package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class ArticlePrintPoolField {
public static FieldIdentifier<ArticlePrintPool,Long> id = new FieldIdentifier<>(ArticlePrintPool.class, Long.class, "id");
public static FieldIdentifier<ArticlePrintPool,Article> article = new FieldIdentifier<>(ArticlePrintPool.class, Article.class, "article");
public static FieldIdentifier<ArticlePrintPool,Integer> number = new FieldIdentifier<>(ArticlePrintPool.class, Integer.class, "number");

}