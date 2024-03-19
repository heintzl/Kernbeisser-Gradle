package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class SupplierField {
  public static FieldIdentifier<Supplier, Integer> id = new FieldIdentifier<>(Supplier.class, "id");
  public static FieldIdentifier<Supplier, String> name =
      new FieldIdentifier<>(Supplier.class, "name");
  public static FieldIdentifier<Supplier, String> phoneNumber =
      new FieldIdentifier<>(Supplier.class, "phoneNumber");
  public static FieldIdentifier<Supplier, String> fax =
      new FieldIdentifier<>(Supplier.class, "fax");
  public static FieldIdentifier<Supplier, String> street =
      new FieldIdentifier<>(Supplier.class, "street");
  public static FieldIdentifier<Supplier, String> location =
      new FieldIdentifier<>(Supplier.class, "location");
  public static FieldIdentifier<Supplier, String> email =
      new FieldIdentifier<>(Supplier.class, "email");
  public static FieldIdentifier<Supplier, String> shortName =
      new FieldIdentifier<>(Supplier.class, "shortName");
  public static FieldIdentifier<Supplier, Double> defaultSurcharge =
      new FieldIdentifier<>(Supplier.class, "defaultSurcharge");
  public static FieldIdentifier<Supplier, String> keeper =
      new FieldIdentifier<>(Supplier.class, "keeper");
  public static FieldIdentifier<Supplier, Instant> createDate =
      new FieldIdentifier<>(Supplier.class, "createDate");
  public static FieldIdentifier<Supplier, Instant> updateDate =
      new FieldIdentifier<>(Supplier.class, "updateDate");
}
