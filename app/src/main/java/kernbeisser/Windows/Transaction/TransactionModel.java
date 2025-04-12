package kernbeisser.Windows.Transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.util.*;
import java.util.function.Predicate;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;

public class TransactionModel implements IModel<TransactionController> {

  @Getter private final User owner;

  @Getter private final TransactionType transactionType;

  TransactionModel(User owner, TransactionType transactionType) {
    this.transactionType = transactionType;
    this.owner = owner;
  }

  private final Collection<Transaction> transactions = new ArrayList<>();

  void addTransaction(Transaction t) {
    transactions.add(t);
  }

  User findUser(String username) throws NoResultException {
    if (username.matches("Benutzer[\\[]\\d+[]]")) {
      return User.getById(Integer.parseInt(username.replace("Benutzer[", "").replace("]", "")));
    }
    return User.getByUsername(username);
  }

  Collection<Transaction> getTransactions() {
    return transactions;
  }

  Map<Transaction, Optional<Exception>> transfer() {
    final Map<Transaction, Optional<Exception>> transactionExceptions = new HashMap<>();
    for (Transaction transaction : transactions) {
      @Cleanup EntityManager em = DBConnection.getEntityManager();
      EntityTransaction et = em.getTransaction();
      et.begin();
      String info = transaction.getInfo();
      if (transactionType == TransactionType.PAYIN && (info == null || info.isEmpty())) {
        info = "Guthabeneinzahlung";
      }
      try {
        Transaction.doTransaction(
            em,
            transaction.getFromUser(),
            transaction.getToUser(),
            transaction.getValue(),
            transactionType,
            info);
        em.flush();
        transactionExceptions.put(transaction, Optional.empty());
        et.commit();
      } catch (Exception e) {
        et.rollback();
        em.close();
        transactionExceptions.put(transaction, Optional.of(e));
      }
    }
    return transactionExceptions;
  }

  double getSum() {
    return transactions.stream().mapToDouble(Transaction::getValue).sum();
  }

  int getCount() {
    return transactions.size();
  }

  public void remove(Transaction selectedTransaction) {
    transactions.remove(selectedTransaction);
  }

  public List<User> getUsers(Predicate<User> filter) {
    return User.getAllUserFullNames(true, true).stream().filter(filter).toList();
  }
}
