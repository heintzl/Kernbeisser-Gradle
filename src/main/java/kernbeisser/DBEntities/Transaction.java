package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Table
@Entity
public class Transaction implements ValueChange{
    @Id
    @GeneratedValue
    private int id;

    @Column
    private double value;

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

    private static void transfer(User from, User to, int value) {
        Transaction transaction = new Transaction();
        transaction.value = value;
        transaction.from = from;
        transaction.to = to;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(transaction);
        em.flush();
        et.commit();
        em.close();
    }

    public static List<Transaction> getAll(String condition) {
        return Tools.getAll(Transaction.class, condition);
    }

    public static void doTransaction(User from, User to, double value,String info) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        if (from != null) {
            UserGroup fromUG = em.find(UserGroup.class, from.getUserGroup().getId());
            fromUG.setValue(fromUG.getValue() - value);
            em.persist(fromUG);
        }
        UserGroup toUG = em.find(UserGroup.class, to.getUserGroup().getId());
        toUG.setValue(toUG.getValue() + value);
        em.persist(toUG);
        Transaction transaction = new Transaction();
        transaction.setValue(value);
        transaction.setTo(to);
        transaction.setFrom(from);
        transaction.setInfo(info);
        em.persist(transaction);
        em.flush();
        et.commit();
        em.close();
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    @Override
    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public Instant getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
