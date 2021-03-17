package kernbeisser.DBEntities;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class Transaction {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_VALUE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_VALUE_WRITE)})
  private double value;

  @Column
  @Enumerated(EnumType.STRING)
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TRANSACTION_TYPE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TRANSACTION_TYPE_WRITE)})
  private TransactionType transactionType;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_FROM_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_FROM_WRITE)})
  private User fromUser;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TO_WRITE)})
  private User toUser;

  @CreationTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_DATE_WRITE)})
  private Instant date;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_INFO_WRITE)})
  private String info;

  public static List<Transaction> getAll(String condition) {
    return Tools.getAll(Transaction.class, condition);
  }

  public String getDescription() {
    return info.isEmpty() ? transactionType.toString() : info;
  }

  public static Transaction doTransaction(
      EntityManager em,
      User from,
      User to,
      double value,
      TransactionType transactionType,
      String info)
      throws InvalidTransactionException {

    if (from.getUserGroup().getId() == to.getUserGroup().getId())
      throw new kernbeisser.Exeptions.InvalidTransactionException();
    UserGroup fromUG = em.find(UserGroup.class, from.getUserGroup().getId());
    UserGroup toUG = em.find(UserGroup.class, to.getUserGroup().getId());

    double minValue = Setting.DEFAULT_MIN_VALUE.getDoubleValue();
    value = Tools.roundCurrency(value);

    if (transactionType != TransactionType.INITIALIZE) {
      if (fromUG.getValue() - value < minValue) {
        if (!from.hasPermission(PermissionKey.GO_UNDER_MIN)) {
          throw new kernbeisser.Exeptions.InvalidTransactionException(
              "the sending user ["
                  + from.getId()
                  + "] has not the Permission to go under the min value of "
                  + minValue
                  + "€");
        }
      }
      if (value < 0 && toUG.getValue() + value < minValue) {
        if (!to.hasPermission(PermissionKey.GO_UNDER_MIN)) {
          throw new kernbeisser.Exeptions.InvalidTransactionException(
              "the receiving user ["
                  + from.getId()
                  + "] has not the Permission to go under the min value of "
                  + minValue
                  + "€");
        }
      }
    }
    Transaction transaction = new Transaction();
    transaction.value = value;
    transaction.toUser = to;
    transaction.fromUser = from;
    transaction.info = info;
    transaction.transactionType = transactionType;
    // isValidTransaction(transaction);
    // sett
    setValue(fromUG, fromUG.getValue() - value);
    setValue(toUG, toUG.getValue() + value);
    em.persist(fromUG);
    em.persist(toUG);
    em.persist(transaction);
    em.flush();
    from.getUserGroup().setValue(from.getUserGroup().getValue() - value);
    to.getUserGroup().setValue(to.getUserGroup().getValue() + value);
    return transaction;
  }

  public static void isValidTransaction(Transaction transaction)
      throws InvalidTransactionException {
    double minValue = Setting.DEFAULT_MIN_VALUE.getDoubleValue();
    if (transaction.getFromUser().getUserGroup().getValue() - transaction.getValue() < minValue) {
      if (!transaction.getFromUser().hasPermission(PermissionKey.GO_UNDER_MIN)) {
        throw new InvalidTransactionException(new PermissionRequired());
      }
    }
    if (transaction.getValue() < 0
        && transaction.getToUser().getUserGroup().getValue() - transaction.getValue() < minValue) {
      if (!transaction.getToUser().hasPermission(PermissionKey.GO_UNDER_MIN)) {
        throw new InvalidTransactionException(new PermissionRequired());
      }
    }
  }

  private static void setValue(UserGroup transaction, double value) {
    try {
      Method method = UserGroup.class.getDeclaredMethod("setValue", double.class);
      method.setAccessible(true);
      method.invoke(transaction, Tools.roundCurrency(value));
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  public static Transaction doPurchaseTransaction(EntityManager em, User customer, double value)
      throws InvalidTransactionException {
    return doTransaction(
        em,
        customer,
        User.getKernbeisserUser(),
        value,
        TransactionType.PURCHASE,
        "Einkauf vom " + LocalDate.now());
  }
}
