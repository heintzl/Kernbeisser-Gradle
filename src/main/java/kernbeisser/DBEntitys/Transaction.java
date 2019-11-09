package kernbeisser.DBEntitys;

import kernbeisser.DBConnection.DBConnection;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;

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

    public Date getDate() {
        return date;
    }
}
