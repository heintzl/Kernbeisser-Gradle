package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;

public class OfferField {
public static FieldIdentifier<Offer,Integer> id = new FieldIdentifier<>(Offer.class, Integer.class, "id");
public static FieldIdentifier<Offer,Instant> fromDate = new FieldIdentifier<>(Offer.class, Instant.class, "fromDate");
public static FieldIdentifier<Offer,Instant> toDate = new FieldIdentifier<>(Offer.class, Instant.class, "toDate");
public static FieldIdentifier<Offer,Article> offerArticle = new FieldIdentifier<>(Offer.class, Article.class, "offerArticle");
public static FieldIdentifier<Offer,Article> parentArticle = new FieldIdentifier<>(Offer.class, Article.class, "parentArticle");
public static FieldIdentifier<Offer,Instant> createDate = new FieldIdentifier<>(Offer.class, Instant.class, "createDate");

}