package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class PurchaseField {
  public static FieldIdentifier<kernbeisser.DBEntities.Purchase, Long> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.Purchase.class, Long.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.Purchase, kernbeisser.DBEntities.SaleSession>
      session =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Purchase.class,
              kernbeisser.DBEntities.SaleSession.class,
              "session");
  public static FieldIdentifier<kernbeisser.DBEntities.Purchase, java.time.Instant> createDate =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Purchase.class, java.time.Instant.class, "createDate");
  public static FieldIdentifier<kernbeisser.DBEntities.Purchase, Double> userSurcharge =
      new FieldIdentifier<>(kernbeisser.DBEntities.Purchase.class, Double.class, "userSurcharge");
  public static FieldIdentifier<kernbeisser.DBEntities.Purchase, java.lang.String>
      sellerIdentification =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Purchase.class,
              java.lang.String.class,
              "sellerIdentification");
  public static FieldIdentifier<kernbeisser.DBEntities.Purchase, java.lang.String>
      customerIdentification =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Purchase.class,
              java.lang.String.class,
              "customerIdentification");
}
