package kernbeisser.DBEntities.Types;

import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.CatalogEntry;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.DBEntities.User;

import java.time.Instant;

public class PreOrderField {
	public static FieldIdentifier<PreOrder, Integer> id = new FieldIdentifier<>(PreOrder.class, "id");
	public static FieldIdentifier<PreOrder, CatalogEntry> catalogEntry = new FieldIdentifier<>(PreOrder.class, "catalogEntry");
	public static FieldIdentifier<PreOrder, Article> article = new FieldIdentifier<>(PreOrder.class, "article");
	public static FieldIdentifier<PreOrder, User> user = new FieldIdentifier<>(PreOrder.class, "user");
	public static FieldIdentifier<PreOrder, String> info = new FieldIdentifier<>(PreOrder.class, "info");
	public static FieldIdentifier<PreOrder, Integer> amount = new FieldIdentifier<>(PreOrder.class, "amount");
	public static FieldIdentifier<PreOrder, Instant> delivery = new FieldIdentifier<>(PreOrder.class, "delivery");
	public static FieldIdentifier<PreOrder, Instant> createDate = new FieldIdentifier<>(PreOrder.class, "createDate");
	public static FieldIdentifier<PreOrder, Instant> orderedOn = new FieldIdentifier<>(PreOrder.class, "orderedOn");
	
	
	
	
	
	
}
