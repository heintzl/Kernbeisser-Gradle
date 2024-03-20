package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBConnection.FieldIdentifier;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.TransactionType;

import java.time.Instant;

public class TransactionField {
public static FieldIdentifier<Transaction,Long> id = new FieldIdentifier<>(Transaction.class, Long.class, "id");
public static FieldIdentifier<Transaction,Double> value = new FieldIdentifier<>(Transaction.class, Double.class, "value");
public static FieldIdentifier<Transaction, TransactionType> transactionType = new FieldIdentifier<>(Transaction.class, TransactionType.class, "transactionType");
public static FieldIdentifier<Transaction,User> fromUser = new FieldIdentifier<>(Transaction.class, User.class, "fromUser");
public static FieldIdentifier<Transaction,User> toUser = new FieldIdentifier<>(Transaction.class, User.class, "toUser");
public static FieldIdentifier<Transaction,UserGroup> fromUserGroup = new FieldIdentifier<>(Transaction.class, UserGroup.class, "fromUserGroup");
public static FieldIdentifier<Transaction,UserGroup> toUserGroup = new FieldIdentifier<>(Transaction.class, UserGroup.class, "toUserGroup");
public static FieldIdentifier<Transaction,Instant> date = new FieldIdentifier<>(Transaction.class, Instant.class, "date");
public static FieldIdentifier<Transaction,String> info = new FieldIdentifier<>(Transaction.class, String.class, "info");
public static FieldIdentifier<Transaction,Long> accountingReportNo = new FieldIdentifier<>(Transaction.class, Long.class, "accountingReportNo");
public static FieldIdentifier<Transaction,User> createdBy = new FieldIdentifier<>(Transaction.class, User.class, "createdBy");
public static FieldIdentifier<Transaction,String> fromIdentification = new FieldIdentifier<>(Transaction.class, String.class, "fromIdentification");
public static FieldIdentifier<Transaction,String> toIdentification = new FieldIdentifier<>(Transaction.class, String.class, "toIdentification");

}