package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Security.Access.UserRelated;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;
import rs.groump.AccessManager;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Table(
    uniqueConstraints = {@UniqueConstraint(name = "UX_transaction_seqNo", columnNames = "seqNo")},
    indexes = {@Index(name = "IX_transaction_date", columnList = "date")})
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class Transaction implements UserRelated {

  @Id
  @Column(updatable = false, insertable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ID_WRITE)})
  private long id;

  // introduced with hibernate 6 to ensure strict temporal sequence,
  // because with the new auto-increment models gaps in id order may be reused
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ID_WRITE)})
  private long seqNo;

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

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ACCOUNTINGREPORTNO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_ACCOUNTINGREPORTNO_WRITE)})
  private Long accountingReportNo;

  @JoinColumn
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_CREATEDBY_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_CREATEDBY_WRITE)})
  private User createdBy;

  public byte relationToUserGroup(UserGroup userGroup) {
    byte result;
    if (fromUserGroup.equals(userGroup)) return -1;
    if (toUserGroup.equals(userGroup)) return 1;
    return 0;
  }

  public byte relationToKernbeisser() {
    return relationToUserGroup(User.getKernbeisserUser().getUserGroup());
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

    if (from.isTestOnly() || to.isTestOnly()) {
      throw new InvalidTransactionException("test users may not participate in transactions");
    }
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
        if (!to.equals(User.getKernbeisserUser()) && !to.mayGoUnderMin()) {
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
    LogInModel.checkRefreshRequirements(fromUG, toUG);
    long lastSeqNo =
        QueryBuilder.select(Transaction_.seqNo)
            .orderBy(Transaction_.seqNo.desc())
            .limit(1)
            .getSingleResultOptional()
            .orElse(0L);
    Transaction transaction = new Transaction();
    transaction.seqNo = lastSeqNo + 1;
    transaction.value = value;
    transaction.toUser = to;
    transaction.fromUser = from;
    transaction.fromUserGroup = fromUG;
    transaction.toUserGroup = toUG;
    transaction.info = info;
    transaction.transactionType = transactionType;
    transaction.createdBy = LogInModel.getLoggedIn();
    changeValue(fromUG, -value);
    changeValue(toUG, value);
    em.persist(fromUG);
    em.persist(toUG);
    em.persist(transaction);
    if (transactionType == TransactionType.PURCHASE && !from.isActive()) {
      from.setActive(true);
      em.merge(from);
    }
    em.flush();

    return transaction;
  }

  private static void changeValue(UserGroup userGroup, double value) {
    try {
      Method method = UserGroup.class.getDeclaredMethod("setValue", double.class);
      method.setAccessible(true);
      rs.groump.Access.runWithAccessManager(
          AccessManager.ACCESS_GRANTED,
          () -> {
            try {
              method.invoke(userGroup, Tools.roundCurrency(userGroup.getValue() + value));
            } catch (IllegalAccessException | InvocationTargetException e) {
              throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
            }
          });
    } catch (NoSuchMethodException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
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
        "Einkauf vom " + Date.INSTANT_DATE.format(LocalDate.now()));
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return (fromUser == null && toUser == null)
        || user.isInRelation(fromUser)
        || user.isInRelation(toUser);
  }
}
