package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;

public class TransactionField {
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, Long> id =
      new FieldIdentifier<>(kernbeisser.DBEntities.Transaction.class, Long.class, "id");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, Double> value =
      new FieldIdentifier<>(kernbeisser.DBEntities.Transaction.class, Double.class, "value");
  public static FieldIdentifier<
          kernbeisser.DBEntities.Transaction, kernbeisser.Enums.TransactionType>
      transactionType =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class,
              kernbeisser.Enums.TransactionType.class,
              "transactionType");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, kernbeisser.DBEntities.User>
      fromUser =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class,
              kernbeisser.DBEntities.User.class,
              "fromUser");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, kernbeisser.DBEntities.User>
      toUser =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class,
              kernbeisser.DBEntities.User.class,
              "toUser");
  public static FieldIdentifier<
          kernbeisser.DBEntities.Transaction, kernbeisser.DBEntities.UserGroup>
      fromUserGroup =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class,
              kernbeisser.DBEntities.UserGroup.class,
              "fromUserGroup");
  public static FieldIdentifier<
          kernbeisser.DBEntities.Transaction, kernbeisser.DBEntities.UserGroup>
      toUserGroup =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class,
              kernbeisser.DBEntities.UserGroup.class,
              "toUserGroup");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, java.time.Instant> date =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Transaction.class, java.time.Instant.class, "date");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, java.lang.String> info =
      new FieldIdentifier<>(
          kernbeisser.DBEntities.Transaction.class, java.lang.String.class, "info");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, java.lang.Long>
      accountingReportNo =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class, java.lang.Long.class, "accountingReportNo");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, kernbeisser.DBEntities.User>
      createdBy =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class,
              kernbeisser.DBEntities.User.class,
              "createdBy");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, java.lang.String>
      fromIdentification =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class,
              java.lang.String.class,
              "fromIdentification");
  public static FieldIdentifier<kernbeisser.DBEntities.Transaction, java.lang.String>
      toIdentification =
          new FieldIdentifier<>(
              kernbeisser.DBEntities.Transaction.class, java.lang.String.class, "toIdentification");
}
