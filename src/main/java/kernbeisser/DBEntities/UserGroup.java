package kernbeisser.DBEntities;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.*;

@Table
@Entity
@NoArgsConstructor
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

  /* for output to Report */
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_VALUE_READ)})
  @Transient
  private String membersAsString;

  @Column
  @Setter(
      onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_WRITE)})
  @Getter(
      onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_READ)})
  private double solidaritySurcharge;

  public UserGroup withMembersAsString(boolean withNames) {
    UserGroup result = new UserGroup();
    result.membersAsString =
        getMembers().stream()
            .map(m -> withNames ? m.getFullName() : String.valueOf(m.getId()))
            .collect(Collectors.joining(", "));
    result.id = getId();
    result.value = getValue();
    return result;
  }

  public static List<UserGroup> getAll(String condition) {
    return Tools.getAll(UserGroup.class, condition);
  }

  public Collection<User> getMembers() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select u from User u where userGroup.id = " + id, User.class)
        .getResultList();
  }

  public double calculateValue() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    double v = 0;
    for (Transaction transaction :
        em.createQuery(
                "select t from Transaction t where t.fromUser = (select u from User u where u.userGroup.id = :ugid) or t.toUser = (select u from User u where u.userGroup.id = :ugid)",
                Transaction.class)
            .getResultList()) {
      v =
          transaction.getFromUser().getUserGroup().id == id
              ? v + transaction.getValue()
              : v - transaction.getValue();
    }
    return v;
  }

  public static Collection<UserGroup> defaultSearch(String s, int i) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select usergroup from UserGroup usergroup where usergroup.id in (select user.userGroup.id from User user where username like :s or firstName like :s or surname like :s)",
            UserGroup.class)
        .setParameter("s", s + "%")
        .setMaxResults(i)
        .getResultList();
  }

  public String getMemberString() {
    Collection<User> members = getMembers();
    StringBuilder sb = new StringBuilder();
    for (User member : members) {
      sb.append(member.getFirstName()).append(" ").append(member.getSurname()).append(", ");
    }
    sb.delete(sb.length() - 2, sb.length());
    return sb.toString();
  }
}
