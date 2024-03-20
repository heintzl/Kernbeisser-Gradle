package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;

public class PreOrderField {
public static FieldIdentifier<PreOrder,Integer> id = new FieldIdentifier<>(PreOrder.class, Integer.class, "id");
public static FieldIdentifier<PreOrder,CatalogEntry> catalogEntry = new FieldIdentifier<>(PreOrder.class, CatalogEntry.class, "catalogEntry");
public static FieldIdentifier<PreOrder,Article> article = new FieldIdentifier<>(PreOrder.class, Article.class, "article");
public static FieldIdentifier<PreOrder,User> user = new FieldIdentifier<>(PreOrder.class, User.class, "user");
public static FieldIdentifier<PreOrder,String> info = new FieldIdentifier<>(PreOrder.class, String.class, "info");
public static FieldIdentifier<PreOrder,Integer> amount = new FieldIdentifier<>(PreOrder.class, Integer.class, "amount");
public static FieldIdentifier<PreOrder,Instant> delivery = new FieldIdentifier<>(PreOrder.class, Instant.class, "delivery");
public static FieldIdentifier<PreOrder,Instant> createDate = new FieldIdentifier<>(PreOrder.class, Instant.class, "createDate");
public static FieldIdentifier<PreOrder,Instant> orderedOn = new FieldIdentifier<>(PreOrder.class, Instant.class, "orderedOn");

}