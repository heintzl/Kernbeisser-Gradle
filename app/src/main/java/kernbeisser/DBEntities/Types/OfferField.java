package kernbeisser.DBEntities.Types;

import kernbeisser.DBEntities.Offer;

public class OfferField {
	public static final FieldIdentifier<Offer> id = new FieldIdentifier<>(Offer.class, "id");
	public static final FieldIdentifier<Offer> fromDate =
			new FieldIdentifier<>(Offer.class, "fromDate");
	public static final FieldIdentifier<Offer> toDate =
			new FieldIdentifier<>(Offer.class, "toDate");
	public static final FieldIdentifier<Offer> offerArticle =
			new FieldIdentifier<>(Offer.class, "offerArticle");
	public static final FieldIdentifier<Offer> parentArticle =
			new FieldIdentifier<>(Offer.class, "parentArticle");
	public static final FieldIdentifier<Offer> createDate =
			new FieldIdentifier<>(Offer.class, "createDate");
}
