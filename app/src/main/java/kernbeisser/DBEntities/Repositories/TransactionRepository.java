package kernbeisser.DBEntities.Repositories;

import static kernbeisser.DBConnection.ExpressionFactory.max;
import static kernbeisser.DBConnection.PredicateFactory.or;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.ExpressionFactory;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.Transaction_;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import lombok.Cleanup;

public class TransactionRepository {

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

  public static void writeAccountingReportNo(Collection<Transaction> transactions, long no) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (Transaction t : transactions) {
      if (t.getAccountingReportNo() == null) {
        t.setAccountingReportNo(no);
        em.merge(t);
      }
    }
  }

    public static boolean isPurchase(Transaction t) {
        return (t.getTransactionType() == TransactionType.PURCHASE);
    }

    public static boolean isAccountingReportTransaction(Transaction t) {
        switch (t.getTransactionType()) {
            case INITIALIZE:
            case PAYIN:
                return true;
            case USER_GENERATED:
                return t.relationToKernbeisser() != 0;
            default:
                return false;
        }
    }
}
