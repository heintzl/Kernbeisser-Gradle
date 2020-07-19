package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Table
@Entity
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_GID_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.USER_GROUP_GID_WRITE)})
    private int gid;

    @Column
    @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_VALUE_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.USER_GROUP_VALUE_WRITE)})
    private double value;

    @Column
    @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_INTEREST_THIS_YEAR_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.USER_GROUP_INTEREST_THIS_YEAR_WRITE)})
    private int interestThisYear;


    public static List<UserGroup> getAll(String condition) {
        return Tools.getAll(UserGroup.class, condition);
    }


    public Collection<User> getMembers() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<User> out = em.createQuery("select u from User u where userGroup.id = " + gid, User.class)
                                 .getResultList();
        em.close();
        return out;
    }


    public double calculateValue() {
        EntityManager em = DBConnection.getEntityManager();
        double v = 0;
        for (Transaction transaction : em.createQuery(
                "select t from Transaction t where t.from = (select u from User u where u.userGroup.id = :ugid) or t.to = (select u from User u where u.userGroup.id = :ugid)",
                Transaction.class).getResultList()) {
            v = transaction.getFrom().getUserGroup().gid == gid
                ? v + transaction.getValue()
                : v - transaction.getValue();
        }
        em.close();
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserGroup userGroup = (UserGroup) o;
        return gid == userGroup.gid &&
               Double.compare(userGroup.value, value) == 0 &&
               interestThisYear == userGroup.interestThisYear;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gid, value, interestThisYear);
    }
}
