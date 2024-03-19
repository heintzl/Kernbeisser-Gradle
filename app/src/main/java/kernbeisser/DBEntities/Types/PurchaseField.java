package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;

public class PurchaseField {
  public static FieldIdentifier<Purchase, Long> id = new FieldIdentifier<>(Purchase.class, "id");
  public static FieldIdentifier<Purchase, SaleSession> session =
      new FieldIdentifier<>(Purchase.class, "session");
  public static FieldIdentifier<Purchase, Instant> createDate =
      new FieldIdentifier<>(Purchase.class, "createDate");
  public static FieldIdentifier<Purchase, Double> userSurcharge =
      new FieldIdentifier<>(Purchase.class, "userSurcharge");
  public static FieldIdentifier<Purchase, String> sellerIdentification =
      new FieldIdentifier<>(Purchase.class, "sellerIdentification");
  public static FieldIdentifier<Purchase, String> customerIdentification =
      new FieldIdentifier<>(Purchase.class, "customerIdentification");
}
