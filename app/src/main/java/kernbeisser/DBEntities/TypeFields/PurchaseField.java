package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

import java.time.Instant;

public class PurchaseField {
public static FieldIdentifier<Purchase,Long> id = new FieldIdentifier<>(Purchase.class, Long.class, "id");
public static FieldIdentifier<Purchase,SaleSession> session = new FieldIdentifier<>(Purchase.class, SaleSession.class, "session");
public static FieldIdentifier<Purchase,Instant> createDate = new FieldIdentifier<>(Purchase.class, Instant.class, "createDate");
public static FieldIdentifier<Purchase,Double> userSurcharge = new FieldIdentifier<>(Purchase.class, Double.class, "userSurcharge");
public static FieldIdentifier<Purchase,String> sellerIdentification = new FieldIdentifier<>(Purchase.class, String.class, "sellerIdentification");
public static FieldIdentifier<Purchase,String> customerIdentification = new FieldIdentifier<>(Purchase.class, String.class, "customerIdentification");

}