package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class PriceListField {
public static FieldIdentifier<kernbeisser.DBEntities.PriceList,Integer> id = new FieldIdentifier<>(kernbeisser.DBEntities.PriceList.class, Integer.class, "id");
public static FieldIdentifier<kernbeisser.DBEntities.PriceList,java.lang.String> name = new FieldIdentifier<>(kernbeisser.DBEntities.PriceList.class, java.lang.String.class, "name");
public static FieldIdentifier<kernbeisser.DBEntities.PriceList,kernbeisser.DBEntities.PriceList> superPriceList = new FieldIdentifier<>(kernbeisser.DBEntities.PriceList.class, kernbeisser.DBEntities.PriceList.class, "superPriceList");
public static FieldIdentifier<kernbeisser.DBEntities.PriceList,java.time.Instant> lastPrint = new FieldIdentifier<>(kernbeisser.DBEntities.PriceList.class, java.time.Instant.class, "lastPrint");
public static FieldIdentifier<kernbeisser.DBEntities.PriceList,java.time.Instant> updateDate = new FieldIdentifier<>(kernbeisser.DBEntities.PriceList.class, java.time.Instant.class, "updateDate");
public static FieldIdentifier<kernbeisser.DBEntities.PriceList,java.time.Instant> createDate = new FieldIdentifier<>(kernbeisser.DBEntities.PriceList.class, java.time.Instant.class, "createDate");

}