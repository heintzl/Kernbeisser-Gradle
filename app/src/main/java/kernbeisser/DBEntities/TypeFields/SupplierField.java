package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class SupplierField {
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.Supplier.class, Integer.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> name =
      new FieldIdentifier<>(kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "name");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> phoneNumber =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "phoneNumber");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> fax =
      new FieldIdentifier<>(kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "fax");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> street =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "street");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> location =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "location");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> email =
      new FieldIdentifier<>(kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "email");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> shortName =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "shortName");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, Double> defaultSurcharge =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, Double.class, "defaultSurcharge");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.lang.String> keeper =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, java.lang.String.class, "keeper");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.time.Instant> createDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, java.time.Instant.class, "createDate");
  public static FieldIdentifier<kernbeisser.DBEntities.Supplier, java.time.Instant> updateDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Supplier.class, java.time.Instant.class, "updateDate");
}
