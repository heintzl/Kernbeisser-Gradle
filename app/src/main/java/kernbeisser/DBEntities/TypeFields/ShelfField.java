package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class ShelfField {
public static FieldIdentifier<kernbeisser.DBEntities.Shelf,Integer> id = new FieldIdentifier<>(kernbeisser.DBEntities.Shelf.class, Integer.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.Shelf,Integer> shelfNo = new FieldIdentifier<>(kernbeisser.DBEntities.Shelf.class, Integer.class, "shelfNo");
public static FieldIdentifier<kernbeisser.DBEntities.Shelf,java.lang.String> location = new FieldIdentifier<>(kernbeisser.DBEntities.Shelf.class, java.lang.String.class, "location");
public static FieldIdentifier<kernbeisser.DBEntities.Shelf,java.lang.String> comment = new FieldIdentifier<>(kernbeisser.DBEntities.Shelf.class, java.lang.String.class, "comment");
public static FieldIdentifier<kernbeisser.DBEntities.Shelf,java.util.Set> priceLists = new FieldIdentifier<>(kernbeisser.DBEntities.Shelf.class, java.util.Set.class, "priceLists");
public static FieldIdentifier<kernbeisser.DBEntities.Shelf,java.util.Set> articles = new FieldIdentifier<>(kernbeisser.DBEntities.Shelf.class, java.util.Set.class, "articles");
public static FieldIdentifier<kernbeisser.DBEntities.Shelf,java.time.Instant> createDate = new FieldIdentifier<>(kernbeisser.DBEntities.Shelf.class, java.time.Instant.class, "createDate");

}