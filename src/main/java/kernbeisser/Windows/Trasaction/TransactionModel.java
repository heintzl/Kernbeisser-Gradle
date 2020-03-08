package kernbeisser.Windows.Trasaction;

import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class TransactionModel implements Model {
    private Collection<Transaction> transactions = new ArrayList<>();

    void addTransaction(Transaction t) {
        transactions.add(t);
    }

    User findUser(String username) {
        List<User> users = User.getAll("where username like '" + username + "'");
        return users != null ? users.get(0) : null;
    }

    Collection<Transaction> getTransactions() {
        return transactions;
    }

    void transfer() {
        for (Transaction transaction : transactions) {
            Transaction.doTransaction(transaction.getFrom(), transaction.getTo(), transaction.getValue());
        }
    }

    public void remove(Transaction selectedTransaction) {
        transactions.remove(selectedTransaction);
    }
}
