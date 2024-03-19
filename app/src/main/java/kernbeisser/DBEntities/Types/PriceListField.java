package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.PriceList;

public class PriceListField {
  public static FieldIdentifier<PriceList, Integer> id =
      new FieldIdentifier<>(PriceList.class, "id");
  public static FieldIdentifier<PriceList, String> name =
      new FieldIdentifier<>(PriceList.class, "name");
  public static FieldIdentifier<PriceList, PriceList> superPriceList =
      new FieldIdentifier<>(PriceList.class, "superPriceList");
  public static FieldIdentifier<PriceList, Instant> lastPrint =
      new FieldIdentifier<>(PriceList.class, "lastPrint");
  public static FieldIdentifier<PriceList, Instant> updateDate =
      new FieldIdentifier<>(PriceList.class, "updateDate");
  public static FieldIdentifier<PriceList, Instant> createDate =
      new FieldIdentifier<>(PriceList.class, "createDate");
}
