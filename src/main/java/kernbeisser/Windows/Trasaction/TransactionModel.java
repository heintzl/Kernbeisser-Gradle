package kernbeisser.Windows.Trasaction;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class TransactionModel implements Model {
    private Collection<Transaction> transactions = new ArrayList<>();
    void addTransaction(Transaction t){
        transactions.add(t);
    }

    User findUser(String username){
        List<User> users = User.getAll("where username like "+username);
        return users!=null ? users.get(0) : null;
    }

    Collection<Transaction> getTransactions() {
        return transactions;
    }

    void transfer() {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        transactions.forEach(em::persist);
        em.flush();
        et.commit();
        em.close();
    }
}
