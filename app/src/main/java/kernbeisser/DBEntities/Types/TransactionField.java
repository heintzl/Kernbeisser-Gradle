package kernbeisser.DBEntities.Types;

import java.time.Instant;
import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.TransactionType;

public class TransactionField {
  public static FieldIdentifier<Transaction, Long> id =
      new FieldIdentifier<>(Transaction.class, "id");
  public static FieldIdentifier<Transaction, Double> value =
      new FieldIdentifier<>(Transaction.class, "value");
  public static FieldIdentifier<Transaction, TransactionType> transactionType =
      new FieldIdentifier<>(Transaction.class, "transactionType");
  public static FieldIdentifier<Transaction, User> fromUser =
      new FieldIdentifier<>(Transaction.class, "fromUser");
  public static FieldIdentifier<Transaction, User> toUser =
      new FieldIdentifier<>(Transaction.class, "toUser");
  public static FieldIdentifier<Transaction, UserGroup> fromUserGroup =
      new FieldIdentifier<>(Transaction.class, "fromUserGroup");
  public static FieldIdentifier<Transaction, UserGroup> toUserGroup =
      new FieldIdentifier<>(Transaction.class, "toUserGroup");
  public static FieldIdentifier<Transaction, Instant> date =
      new FieldIdentifier<>(Transaction.class, "date");
  public static FieldIdentifier<Transaction, String> info =
      new FieldIdentifier<>(Transaction.class, "info");
  public static FieldIdentifier<Transaction, Long> accountingReportNo =
      new FieldIdentifier<>(Transaction.class, "accountingReportNo");
  public static FieldIdentifier<Transaction, User> createdBy =
      new FieldIdentifier<>(Transaction.class, "createdBy");
  public static FieldIdentifier<Transaction, String> fromIdentification =
      new FieldIdentifier<>(Transaction.class, "fromIdentification");
  public static FieldIdentifier<Transaction, String> toIdentification =
      new FieldIdentifier<>(Transaction.class, "toIdentification");
}
