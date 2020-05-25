package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Table
@Entity
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int gid;

    @Column
    private double value;

    @Column
    private int interestThisYear;

    public static List<UserGroup> getAll(String condition) {
        return Tools.getAll(UserGroup.class, condition);
    }

    public double getValue() {
        return value;
    }

    private void setValue(double value) {
        this.value = value;
    }

    public int getId() {
        return gid;
    }

    public int getInterestThisYear() {
        return interestThisYear;
    }

    public void setInterestThisYear(int interestThisYear) {
        this.interestThisYear = interestThisYear;
    }

    public Collection<User> getMembers() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<User> out = em.createQuery("select u from User u where userGroup.id = " + gid, User.class)
                                 .getResultList();
        em.close();
        return out;
    }


    public double calculateValue(){
        EntityManager em = DBConnection.getEntityManager();
        double v = 0;
        for (Transaction transaction : em.createQuery(
                "select t from Transaction t where t.from = (select u from User u where u.userGroup.id = :ugid) or t.to = (select u from User u where u.userGroup.id = :ugid)",
                Transaction.class).getResultList()) {
            v = transaction.getFrom().getUserGroup().gid == gid ? v+transaction.getValue() : v-transaction.getValue();
        }
        em.close();
        return v;
    }

}