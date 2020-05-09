package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Table
@Entity
public class Transaction  {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private double value;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @JoinColumn
    @ManyToOne
    private User from;

    @JoinColumn(nullable = false)
    @ManyToOne
    private User to;

    @CreationTimestamp
    private Instant date;

    @Column
    private String info;



    public static List<Transaction> getAll(String condition) {
        return Tools.getAll(Transaction.class, condition);
    }

    public static void doTransaction(User from, User to, double value,TransactionType transactionType, String info) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        UserGroup fromUG = em.find(UserGroup.class, from.getUserGroup().getId());
        fromUG.setValue(fromUG.getValue() - value);em.persist(fromUG);
        UserGroup toUG = em.find(UserGroup.class, to.getUserGroup().getId());
        toUG.setValue(toUG.getValue() + value);
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

    public static void doPurchaseTransaction(User customer,double value){
        doTransaction(customer,User.getKernbeisserUser(),value,TransactionType.PURCHASE, "Einkauf vom " + LocalDate.now());
    }

    public Instant getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public double getValue() {
        return value;
    }
}
