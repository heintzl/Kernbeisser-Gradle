package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;

public class OfferField {
  public static final FieldIdentifier<Offer, Integer> id = new FieldIdentifier<>(Offer.class, "id");
  public static final FieldIdentifier<Offer, Instant> fromDate =
      new FieldIdentifier<>(Offer.class, "fromDate");
  public static final FieldIdentifier<Offer, Instant> toDate =
      new FieldIdentifier<>(Offer.class, "toDate");
  public static final FieldIdentifier<Offer, Article> offerArticle =
      new FieldIdentifier<>(Offer.class, "offerArticle");
  public static final FieldIdentifier<Offer, Article> parentArticle =
      new FieldIdentifier<>(Offer.class, "parentArticle");
  public static final FieldIdentifier<Offer, Instant> createDate =
      new FieldIdentifier<>(Offer.class, "createDate");
}
