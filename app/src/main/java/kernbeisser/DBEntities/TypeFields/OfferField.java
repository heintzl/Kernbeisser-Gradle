package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class OfferField {
  public static FieldIdentifier<kernbeisser.DBEntities.Offer, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.Offer.class, Integer.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.Offer, java.time.Instant> fromDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Offer.class, java.time.Instant.class, "fromDate");
  public static FieldIdentifier<kernbeisser.DBEntities.Offer, java.time.Instant> toDate =
      new FieldIdentifier<>(kernbeisser.DBEntities.Offer.class, java.time.Instant.class, "toDate");
  public static FieldIdentifier<kernbeisser.DBEntities.Offer, kernbeisser.DBEntities.Article>
      offerArticle =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Offer.class,
              kernbeisser.DBEntities.Article.class,
              "offerArticle");
  public static FieldIdentifier<kernbeisser.DBEntities.Offer, kernbeisser.DBEntities.Article>
      parentArticle =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Offer.class,
              kernbeisser.DBEntities.Article.class,
              "parentArticle");
  public static FieldIdentifier<kernbeisser.DBEntities.Offer, java.time.Instant> createDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Offer.class, java.time.Instant.class, "createDate");
}
