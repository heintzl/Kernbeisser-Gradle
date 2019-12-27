package kernbeisser.DBEntitys;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Table
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int value;

    @JoinColumn
    @ManyToOne
    private User from;

    @JoinColumn(nullable = false)
    @ManyToOne
    private User to;

    @CreationTimestamp
    private Date date;

    private static void transfer(User from,User to,int value){
        Transaction transaction = new Transaction();
        transaction.value=value;
        transaction.from=from;
        transaction.to=to;
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(transaction);
        em.flush();
        et.commit();
        em.close();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public static List<Transaction> getAll(String condition){
        return Tools.getAll(Transaction.class,condition);
    }
}
