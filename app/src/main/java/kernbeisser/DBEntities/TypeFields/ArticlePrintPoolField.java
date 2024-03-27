package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class ArticlePrintPoolField {
  public static FieldIdentifier<kernbeisser.DBEntities.ArticlePrintPool, Long> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.ArticlePrintPool.class, Long.class, "id");
  public static FieldIdentifier<
          kernbeisser.DBEntities.ArticlePrintPool, kernbeisser.DBEntities.Article>
      article =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.ArticlePrintPool.class,
              kernbeisser.DBEntities.Article.class,
              "article");
  public static FieldIdentifier<kernbeisser.DBEntities.ArticlePrintPool, Integer> number =
      new FieldIdentifier<>(kernbeisser.DBEntities.ArticlePrintPool.class, Integer.class, "number");
}
