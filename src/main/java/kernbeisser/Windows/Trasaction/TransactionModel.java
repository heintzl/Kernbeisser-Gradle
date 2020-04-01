package kernbeisser.Windows.Trasaction;

import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class TransactionModel implements Model {
    private Collection<Transaction> transactions = new ArrayList<>();

    void addTransaction(Transaction t) {
        transactions.add(t);
    }

    User findUser(String username) throws NoResultException {
        return User.getByUsername(username);
    }

    Collection<Transaction> getTransactions() {
        return transactions;
    }

    void transfer() {
        for (Transaction transaction : transactions) {
            Transaction.doTransaction(transaction.getFrom(), transaction.getTo(), transaction.getValue());
        }
    }

    double getSum(){
        return transactions.stream().mapToDouble(Transaction::getValue).sum();
    }

    int getCount(){
        return transactions.size();
    }

    public void remove(Transaction selectedTransaction) {
        transactions.remove(selectedTransaction);
    }
}
