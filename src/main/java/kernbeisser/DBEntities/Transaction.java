package kernbeisser.DBEntities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Security.Key;
import kernbeisser.Security.Proxy;
import kernbeisser.Security.Relations.UserRelated;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class Transaction implements UserRelated {

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

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_FROMUG_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_FROMUG_WRITE)})
  private UserGroup fromUserGroup;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TOUG_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TOUG_WRITE)})
  private UserGroup toUserGroup;

  @CreationTimestamp
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_DATE_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_DATE_WRITE)})
  private Instant date;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_INFO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_INFO_WRITE)})
  private String info;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_CREATEDBY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_CREATEDBY_WRITE)})
  private User createdBy;

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

    UserGroup fromUG = em.find(UserGroup.class, from.getUserGroup().getId());
    UserGroup toUG = em.find(UserGroup.class, to.getUserGroup().getId());
    if (fromUG.equals(toUG)) {
      throw new InvalidTransactionException(
          "sending and receiving UserGroup must not be identical");
    }
    double minValue = Setting.DEFAULT_MIN_VALUE.getDoubleValue();
    value = Tools.roundCurrency(value);

    if (transactionType != TransactionType.INITIALIZE) {
      if (fromUG.getValue() - value < minValue) {
        if (fromUG.getMembers().stream().noneMatch(User::mayGoUnderMin)) {
          throw new kernbeisser.Exeptions.InvalidTransactionException(
              "the sending user ["
                  + from.getId()
                  + "] has not the Permission to go under the min value of "
                  + minValue
                  + "€");
        }
      }
      if (value < 0 && toUG.getValue() + value < minValue) {
        if (!to.mayGoUnderMin()) {
          throw new kernbeisser.Exeptions.InvalidTransactionException(
              "the receiving user ["
                  + from.getId()
                  + "] has not the Permission to go under the min value of "
                  + minValue
                  + "€");
        }
      }
    }
    return saveTransaction(em, from, to, value, transactionType, info, fromUG, toUG);
  }

  public static Transaction switchGroupTransaction(
      EntityManager em, User user, UserGroup fromUG, UserGroup toUG, double value)
      throws InvalidTransactionException {
    if (fromUG.equals(toUG)) {
      throw new InvalidTransactionException(
          "sending and receiving UserGroup must not be identical");
    }
    String info = "Konto-Übertrag von " + user.getFullName();
    return saveTransaction(em, user, user, value, TransactionType.GROUP_MERGE, info, fromUG, toUG);
  }

  @NotNull
  private static Transaction saveTransaction(
      EntityManager em,
      User from,
      User to,
      double value,
      TransactionType transactionType,
      String info,
      UserGroup fromUG,
      UserGroup toUG) {
    Transaction transaction = new Transaction();
    transaction.value = value;
    transaction.toUser = to;
    transaction.fromUser = from;
    transaction.fromUserGroup = fromUG;
    transaction.toUserGroup = toUG;
    transaction.info = info;
    transaction.transactionType = transactionType;
    transaction.createdBy = LogInModel.getLoggedIn();
    setValue(fromUG, fromUG.getValue() - value);
    setValue(toUG, toUG.getValue() + value);
    em.persist(fromUG);
    em.persist(toUG);
    em.persist(transaction);
    em.flush();
    Proxy.refresh(from);
    Proxy.refresh(to);
    return transaction;
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

  @Override
  public boolean isInRelation(@NotNull User user) {
    return fromUserGroup.equals(user.getUserGroup()) || toUserGroup.equals(user.getUserGroup());
  }
}
