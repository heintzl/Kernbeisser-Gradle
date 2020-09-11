package kernbeisser.DBEntities;

import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class UserGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_GROUP_ID_WRITE)})
  private int id;

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
    Collection<User> out =
        em.createQuery("select u from User u where userGroup.id = " + id, User.class)
            .getResultList();
    em.close();
    return out;
  }

  public double calculateValue() {
    EntityManager em = DBConnection.getEntityManager();
    double v = 0;
    for (Transaction transaction :
        em.createQuery(
                "select t from Transaction t where t.from = (select u from User u where u.userGroup.id = :ugid) or t.to = (select u from User u where u.userGroup.id = :ugid)",
                Transaction.class)
            .getResultList()) {
      v =
          transaction.getFrom().getUserGroup().id == id
              ? v + transaction.getValue()
              : v - transaction.getValue();
    }
    em.close();
    return v;
  }
}
