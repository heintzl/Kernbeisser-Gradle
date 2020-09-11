package kernbeisser.DBEntities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
  private User from;

  @JoinColumn(nullable = false)
  @ManyToOne
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TO_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.TRANSACTION_TO_WRITE)})
  private User to;

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

  public static void doTransaction(
      User from, User to, double value, TransactionType transactionType, String info)
      throws AccessDeniedException {
    EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    UserGroup fromUG = em.find(UserGroup.class, from.getUserGroup().getId());
    UserGroup toUG = em.find(UserGroup.class, to.getUserGroup().getId());
    double minValue = Setting.DEFAULT_MIN_VALUE.getDoubleValue();
    if (transactionType != TransactionType.INITIALIZE) {
      if (fromUG.getValue() - value < minValue) {
        if (!from.hasPermission(PermissionKey.GO_UNDER_MIN)) {
          throw new AccessDeniedException(
              "the sending user ["
                  + from.getId()
                  + "] has not the Permission to go under the min value of "
                  + minValue
                  + "€");
        }
      }
      if (toUG.getValue() + value < minValue) {
        if (!to.hasPermission(PermissionKey.GO_UNDER_MIN)) {
          throw new AccessDeniedException(
              "the receiving user ["
                  + from.getId()
                  + "] has not the Permission to go under the min value of "
                  + minValue
                  + "€");
        }
      }
    }
    et.begin();
    setValue(fromUG, fromUG.getValue() - value);
    setValue(toUG, toUG.getValue() + value);
    em.persist(fromUG);
    em.persist(toUG);
    Transaction transaction = new Transaction();
    transaction.value = value;
    transaction.to = to;
    transaction.from = from;
    transaction.info = info;
    transaction.transactionType = transactionType;
    em.persist(transaction);
    em.flush();
    et.commit();
    em.close();
  }

  public static boolean isValidTransaction(Transaction transaction) {
    double minValue = Setting.DEFAULT_MIN_VALUE.getDoubleValue();
    if (transaction.getFrom().getUserGroup().getValue() - transaction.getValue() < minValue) {
      if (!transaction.getFrom().hasPermission(PermissionKey.GO_UNDER_MIN)) {
        return false;
      }
    }
    if (transaction.getValue() < 0
        && transaction.getTo().getUserGroup().getValue() - transaction.getValue() < minValue) {
      return transaction.getTo().hasPermission(PermissionKey.GO_UNDER_MIN);
    }
    return true;
  }

  private static void setValue(UserGroup transaction, double value) {
    try {
      Method method = UserGroup.class.getDeclaredMethod("setValue", double.class);
      method.setAccessible(true);
      method.invoke(transaction, value);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  public static void doPurchaseTransaction(User customer, double value)
      throws AccessDeniedException {
    doTransaction(
        customer,
        User.getKernbeisserUser(),
        value,
        TransactionType.PURCHASE,
        "Einkauf vom " + LocalDate.now());
  }
}
