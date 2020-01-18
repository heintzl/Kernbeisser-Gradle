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
    private int value;

    @Column
    private int interestThisYear;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
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

    public Collection<User> getMembers(){
        EntityManager em = DBConnection.getEntityManager();
        Collection<User> out = em.createQuery("select u from User u where userGroup.id = "+gid,User.class).getResultList();
        em.close();
        return out;
    }

    public static List<UserGroup> getAll(String condition){
        return Tools.getAll(UserGroup.class,condition);
    }

}