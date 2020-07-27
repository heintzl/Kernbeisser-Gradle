package kernbeisser.Windows.Trasaction;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.NoResultException;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Windows.Model;

public class TransactionModel implements Model<TransactionController> {

  private final User owner;

  TransactionModel(User owner) {

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

  void transfer() throws AccessDeniedException {
    final boolean[] correct = {true};
    transactions.forEach(e -> correct[0] = correct[0] & Transaction.isValidTransaction(e));
    if (!correct[0]) {
      throw new AccessDeniedException("Not all transactions have valid values");
    }
    for (Transaction transaction : transactions) {
      Transaction.doTransaction(
          transaction.getFrom(),
          transaction.getTo(),
          transaction.getValue(),
          transaction.getTransactionType(),
          transaction.getInfo());
    }
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

  public User getOwner() {
    return owner;
  }
}
