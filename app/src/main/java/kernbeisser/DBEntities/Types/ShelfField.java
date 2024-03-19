package kernbeisser.DBEntities.Types;

import java.time.Instant;
import java.util.Set;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class ShelfField {
  public static FieldIdentifier<Shelf, Integer> id = new FieldIdentifier<>(Shelf.class, "id");
  public static FieldIdentifier<Shelf, Integer> shelfNo =
      new FieldIdentifier<>(Shelf.class, "shelfNo");
  public static FieldIdentifier<Shelf, String> location =
      new FieldIdentifier<>(Shelf.class, "location");
  public static FieldIdentifier<Shelf, String> comment =
      new FieldIdentifier<>(Shelf.class, "comment");
  public static FieldIdentifier<Shelf, Set> priceLists =
      new FieldIdentifier<>(Shelf.class, "priceLists");
  public static FieldIdentifier<Shelf, Set> articles =
      new FieldIdentifier<>(Shelf.class, "articles");
  public static FieldIdentifier<Shelf, Instant> createDate =
      new FieldIdentifier<>(Shelf.class, "createDate");
}
