package kernbeisser.DBEntities;

import static kernbeisser.DBConnection.ExpressionFactory.max;
import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.ExpressionFactory;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.UserNameObfuscation;
import kernbeisser.Security.Access.UserRelated;
import kernbeisser.Useful.Date;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.Cleanup;
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

  /* the following member variables are used to pass non static values to reports */
  @Column @Transient @Getter private String fromIdentification;

  @Column @Transient @Getter private String toIdentification;

  public Transaction withUserIdentifications(UserNameObfuscation withNames) {

    Transaction out = this;
    User from = fromUser;
    User to = toUser;
    switch (withNames) {
      case NONE:
        out.fromIdentification = from.getFullName();
        out.toIdentification = to.getFullName();
        break;
      case WITHOUTPAYIN:
        out.fromIdentification = Integer.toString(from.getId());
        if (out.transactionType == TransactionType.PAYIN) {
          out.toIdentification = to.getFullName();
        } else {
          out.toIdentification = Integer.toString(from.getId());
        }
        break;
      default:
        out.fromIdentification = Integer.toString(from.getId());
        out.toIdentification = Integer.toString(to.getId());
    }
    return out;
  }

  public byte relationToUserGroup(UserGroup userGroup) {
    byte result;
    if (fromUserGroup.equals(userGroup)) return -1;
    if (toUserGroup.equals(userGroup)) return 1;
    return 0;
  }

  public byte relationToKernbeisser() {
    return relationToUserGroup(User.getKernbeisserUser().getUserGroup());
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

  private static void changeValue(UserGroup transaction, double value) {
    try {
      Method method = UserGroup.class.getDeclaredMethod("setValue", double.class);
      method.setAccessible(true);
      rs.groump.Access.runWithAccessManager(
          AccessManager.ACCESS_GRANTED,
          () -> {
            try {
              method.invoke(transaction, Tools.roundCurrency(transaction.getValue() + value));
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

  public static long getLastReportNo() {
    return QueryBuilder.select(Transaction.class, max(Transaction_.accountingReportNo))
        .getSingleResultOptional()
        .map(tuple -> tuple.get(0, Long.class))
        .orElse(0L);
  }

  public static Instant getLastOfReportNo(long reportNo) throws NoResultException {
    return QueryBuilder.select(Transaction.class, max(Transaction_.date))
        .where(
            PredicateFactory.lessOrEq(
                Transaction_.accountingReportNo, ExpressionFactory.asExpression(reportNo)))
        .getSingleResult()
        .get(0, Instant.class);
  }

  public static void writeAccountingReportNo(Collection<Transaction> transactions, long no) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Transaction t : transactions) {
      if (t.accountingReportNo == null) {
        t.setAccountingReportNo(no);
        em.merge(t);
      }
    }
  }

  public static List<Transaction> getTransactionsByReportNo(long reportNo) {
    List<Transaction> transactions =
        QueryBuilder.selectAll(Transaction.class)
            .where(Transaction_.accountingReportNo.eq(reportNo))
            .orderBy(Transaction_.seqNo.asc())
            .getResultList();
    if (transactions.isEmpty()) {
      throw new NoTransactionsFoundException();
    }
    return transactions;
  }

  public static List<Transaction> getUnreportedTransactions() {
    User kbUser = User.getKernbeisserUser();
    List<Transaction> transactions =
        QueryBuilder.selectAll(Transaction.class)
            .where(
                Transaction_.accountingReportNo.isNull(),
                or(Transaction_.fromUser.eq(kbUser), Transaction_.toUser.eq(kbUser)))
            .orderBy(Transaction_.seqNo.asc())
            .getResultList();
    if (transactions.isEmpty()) {
      throw new NoTransactionsFoundException();
    }
    return transactions;
  }

  public boolean isPurchase() {
    return (transactionType == TransactionType.PURCHASE);
  }

  public boolean isAccountingReportTransaction() {
    switch (transactionType) {
      case INITIALIZE:
      case PAYIN:
        return true;
      case USER_GENERATED:
        return relationToKernbeisser() != 0;
      default:
        return false;
    }
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return (fromUser == null && toUser == null)
        || user.isInRelation(fromUser)
        || user.isInRelation(toUser);
  }
}
