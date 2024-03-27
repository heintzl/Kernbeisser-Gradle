package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class SaleSessionField {
  public static FieldIdentifier<kernbeisser.DBEntities.SaleSession, Integer> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.SaleSession.class, Integer.class, "id");
  public static FieldIdentifier<
          kernbeisser.DBEntities.SaleSession, kernbeisser.Enums.SaleSessionType>
      sessionType =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.SaleSession.class,
              kernbeisser.Enums.SaleSessionType.class,
              "sessionType");
  public static FieldIdentifier<kernbeisser.DBEntities.SaleSession, kernbeisser.DBEntities.User>
      customer =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.SaleSession.class,
              kernbeisser.DBEntities.User.class,
              "customer");
  public static FieldIdentifier<kernbeisser.DBEntities.SaleSession, kernbeisser.DBEntities.User>
      secondSeller =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.SaleSession.class,
              kernbeisser.DBEntities.User.class,
              "secondSeller");
  public static FieldIdentifier<kernbeisser.DBEntities.SaleSession, kernbeisser.DBEntities.User>
      seller =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.SaleSession.class,
              kernbeisser.DBEntities.User.class,
              "seller");
  public static FieldIdentifier<
          kernbeisser.DBEntities.SaleSession, kernbeisser.DBEntities.Transaction>
      transaction =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.SaleSession.class,
              kernbeisser.DBEntities.Transaction.class,
              "transaction");
}
