package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Key;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    @JoinColumn(nullable = false)
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

    public static void doTransaction(User from, User to, double value,TransactionType transactionType, String info)
            throws AccessDeniedException {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        UserGroup fromUG = em.find(UserGroup.class, from.getUserGroup().getId());
        UserGroup toUG = em.find(UserGroup.class, to.getUserGroup().getId());
        double minValue = Setting.DEFAULT_MIN_VALUE.getDoubleValue();
        if(fromUG.getValue()-value < minValue){
            if (!from.hasPermission(Key.GO_UNDER_MIN)) throw new AccessDeniedException("the sending user ["+from.getId()+"] has not the Permission to go under the min value of "+minValue+"€");
        }
        if(toUG.getValue()+value < minValue){
            if (!to.hasPermission(Key.GO_UNDER_MIN)) throw new AccessDeniedException("the receiving user ["+from.getId()+"] has not the Permission to go under the min value of "+minValue+"€");
        }
        et.begin();
        setValue(fromUG,fromUG.getValue()-value);
        setValue(toUG,toUG.getValue()+value);
        em.persist(fromUG);
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

    public static boolean isValidTransaction(Transaction transaction){
        double minValue = Setting.DEFAULT_MIN_VALUE.getDoubleValue();
        if(transaction.getFrom().getUserGroup().getValue() - transaction.getValue() < minValue){
            if (!transaction.getFrom().hasPermission(Key.GO_UNDER_MIN)) return false;
        }
        if(transaction.getTo().getUserGroup().getValue() - transaction.getValue() < minValue){
            return transaction.getTo().hasPermission(Key.GO_UNDER_MIN);
        }
        return true;
    }

    private static void setValue(UserGroup transaction,double value){
        try {
            Method method = UserGroup.class.getDeclaredMethod("setValue", double.class);
            method.setAccessible(true);
            method.invoke(transaction, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Tools.showUnexpectedErrorWarning(e);
        }
    }

    public static void doPurchaseTransaction(User customer,double value) throws AccessDeniedException {
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
