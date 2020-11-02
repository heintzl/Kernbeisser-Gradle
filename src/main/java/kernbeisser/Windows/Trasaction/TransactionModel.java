package kernbeisser.Windows.Trasaction;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.NoResultException;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidTransactionException;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.MVC.IModel;

public class TransactionModel implements IModel<TransactionController> {

  private final User owner;

  private final TransactionType transactionType;

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

  void transfer() throws InvalidTransactionException {
    final boolean[] correct = {true};
    transactions.forEach(e -> correct[0] = correct[0] & Transaction.isValidTransaction(e));
    if (!correct[0]) {
      throw new PermissionKeyRequiredException("Not all transactions have valid values");
    }
    for (Transaction transaction : transactions) {
      Transaction.doTransaction(
          transaction.getFrom(),
          transaction.getTo(),
          transaction.getValue(),
          transactionType,
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
