package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;

public class PriceListField {
public static FieldIdentifier<PriceList,Integer> id = new FieldIdentifier<>(PriceList.class, Integer.class, "id");
public static FieldIdentifier<PriceList,String> name = new FieldIdentifier<>(PriceList.class, String.class, "name");
public static FieldIdentifier<PriceList,PriceList> superPriceList = new FieldIdentifier<>(PriceList.class, PriceList.class, "superPriceList");
public static FieldIdentifier<PriceList,Instant> lastPrint = new FieldIdentifier<>(PriceList.class, Instant.class, "lastPrint");
public static FieldIdentifier<PriceList,Instant> updateDate = new FieldIdentifier<>(PriceList.class, Instant.class, "updateDate");
public static FieldIdentifier<PriceList,Instant> createDate = new FieldIdentifier<>(PriceList.class, Instant.class, "createDate");

}