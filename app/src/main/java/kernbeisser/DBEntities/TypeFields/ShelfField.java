package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;
import java.util.Set;

public class ShelfField {
public static FieldIdentifier<Shelf,Integer> id = new FieldIdentifier<>(Shelf.class, Integer.class, "id");
public static FieldIdentifier<Shelf,Integer> shelfNo = new FieldIdentifier<>(Shelf.class, Integer.class, "shelfNo");
public static FieldIdentifier<Shelf,String> location = new FieldIdentifier<>(Shelf.class, String.class, "location");
public static FieldIdentifier<Shelf,String> comment = new FieldIdentifier<>(Shelf.class, String.class, "comment");
public static FieldIdentifier<Shelf,Set> priceLists = new FieldIdentifier<>(Shelf.class, Set.class, "priceLists");
public static FieldIdentifier<Shelf,Set> articles = new FieldIdentifier<>(Shelf.class, Set.class, "articles");
public static FieldIdentifier<Shelf,Instant> createDate = new FieldIdentifier<>(Shelf.class, Instant.class, "createDate");

}