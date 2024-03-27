package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class PreOrderField {
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.PreOrder.class, Integer.class, "id");
  public static FieldIdentifier<
          kernbeisser.DBEntities.PreOrder, kernbeisser.DBEntities.CatalogEntry>
      catalogEntry =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.PreOrder.class,
              kernbeisser.DBEntities.CatalogEntry.class,
              "catalogEntry");
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, kernbeisser.DBEntities.Article>
      article =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.PreOrder.class,
              kernbeisser.DBEntities.Article.class,
              "article");
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, kernbeisser.DBEntities.User> user =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.PreOrder.class, kernbeisser.DBEntities.User.class, "user");
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, java.lang.String> info =
      new FieldIdentifier<>(kernbeisser.DBEntities.PreOrder.class, java.lang.String.class, "info");
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, Integer> amount =
      new FieldIdentifier<>(kernbeisser.DBEntities.PreOrder.class, Integer.class, "amount");
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, java.time.Instant> delivery =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.PreOrder.class, java.time.Instant.class, "delivery");
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, java.time.Instant> createDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.PreOrder.class, java.time.Instant.class, "createDate");
  public static FieldIdentifier<kernbeisser.DBEntities.PreOrder, java.time.Instant> orderedOn =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.PreOrder.class, java.time.Instant.class, "orderedOn");
}
