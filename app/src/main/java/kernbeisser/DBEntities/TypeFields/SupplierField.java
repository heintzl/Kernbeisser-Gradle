package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;

public class SupplierField {
public static FieldIdentifier<Supplier,Integer> id = new FieldIdentifier<>(Supplier.class, Integer.class, "id");
public static FieldIdentifier<Supplier,String> name = new FieldIdentifier<>(Supplier.class, String.class, "name");
public static FieldIdentifier<Supplier,String> phoneNumber = new FieldIdentifier<>(Supplier.class, String.class, "phoneNumber");
public static FieldIdentifier<Supplier,String> fax = new FieldIdentifier<>(Supplier.class, String.class, "fax");
public static FieldIdentifier<Supplier,String> street = new FieldIdentifier<>(Supplier.class, String.class, "street");
public static FieldIdentifier<Supplier,String> location = new FieldIdentifier<>(Supplier.class, String.class, "location");
public static FieldIdentifier<Supplier,String> email = new FieldIdentifier<>(Supplier.class, String.class, "email");
public static FieldIdentifier<Supplier,String> shortName = new FieldIdentifier<>(Supplier.class, String.class, "shortName");
public static FieldIdentifier<Supplier,Double> defaultSurcharge = new FieldIdentifier<>(Supplier.class, Double.class, "defaultSurcharge");
public static FieldIdentifier<Supplier,String> keeper = new FieldIdentifier<>(Supplier.class, String.class, "keeper");
public static FieldIdentifier<Supplier,Instant> createDate = new FieldIdentifier<>(Supplier.class, Instant.class, "createDate");
public static FieldIdentifier<Supplier,Instant> updateDate = new FieldIdentifier<>(Supplier.class, Instant.class, "updateDate");

}