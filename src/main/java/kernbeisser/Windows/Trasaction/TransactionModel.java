package kernbeisser.Windows.Trasaction;

import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collection;

public class TransactionModel implements Model<TransactionController> {


    private final User owner;

    TransactionModel(User owner){

        this.owner = owner;
    }

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
            Transaction.doTransaction(transaction.getFrom(), transaction.getTo(), transaction.getValue(),transaction.getInfo());
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

    public User getOwner() {
        return owner;
    }
}
