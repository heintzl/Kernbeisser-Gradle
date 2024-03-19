package kernbeisser.DBEntities.Types;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.SaleSessionType;

public class SaleSessionField {
  public static FieldIdentifier<SaleSession, Integer> id =
      new FieldIdentifier<>(SaleSession.class, "id");
  public static FieldIdentifier<SaleSession, SaleSessionType> sessionType =
      new FieldIdentifier<>(SaleSession.class, "sessionType");
  public static FieldIdentifier<SaleSession, User> customer =
      new FieldIdentifier<>(SaleSession.class, "customer");
  public static FieldIdentifier<SaleSession, User> secondSeller =
      new FieldIdentifier<>(SaleSession.class, "secondSeller");
  public static FieldIdentifier<SaleSession, User> seller =
      new FieldIdentifier<>(SaleSession.class, "seller");
  public static FieldIdentifier<SaleSession, Transaction> transaction =
      new FieldIdentifier<>(SaleSession.class, "transaction");
}
