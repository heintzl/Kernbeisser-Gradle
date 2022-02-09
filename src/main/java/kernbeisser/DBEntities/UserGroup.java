package kernbeisser.DBEntities;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.InconsistentUserGroupValueException;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Security.Key;
import kernbeisser.Security.Relations.UserRelated;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;

@Table
@Entity
@EqualsAndHashCode(doNotUseGetters = true)
public class UserGroup implements UserRelated {

  public UserGroup() {
    this.value = 0.0;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_GROUP_ID_WRITE)})
  private int id;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_VALUE_READ)})
  @Setter(
      value = AccessLevel.PRIVATE,
      onMethod_ = {@Key(PermissionKey.USER_GROUP_VALUE_WRITE)})
  private double value;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.USER_GROUP_INTEREST_THIS_YEAR_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.USER_GROUP_INTEREST_THIS_YEAR_WRITE)})
  private double interestThisYear;

  @UpdateTimestamp
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_UPDATE_DATE_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_UPDATE_DATE_READ)})
  private Instant updateDate;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @JoinColumn(nullable = true)
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_UPDATE_BY_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_UPDATE_BY_READ)})
  private User updateBy;

  /* for output to Report */
  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.USER_SURNAME_READ)})
  @Transient
  private String membersAsString;

  @Column
  @Getter(onMethod_ = {@Key(PermissionKey.TRANSACTION_VALUE_READ)})
  @Transient
  private Double transactionSum;

  @Column
  @Setter(
      onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_WRITE)})
  @Getter(
      onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_READ)})
  private double solidaritySurcharge;

  @Transient private Optional<Double> oldSolidarity;

  @PostLoad
  private void rememberValues() {
    oldSolidarity = Optional.of(solidaritySurcharge);
  }

  @PreUpdate
  @PrePersist
  private void setUpdateBy() {
    if (!Optional.of(solidaritySurcharge).equals(oldSolidarity)) {
      if (LogInModel.getLoggedIn() != null) updateBy = LogInModel.getLoggedIn();
    }
  }

  public UserGroup withMembersAsStyledString(boolean withNames) {
    UserGroup result = new UserGroup();
    result.membersAsString =
        getMembers().stream()
            .map(
                m ->
                    Tools.jasperTaggedStyling(
                        withNames ? m.getFullName() : String.valueOf(m.getId()),
                        m.isFullMember() ? "" : "i"))
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
      sb.append(member.getFullName()).append(", ");
    }
    sb.delete(sb.length() - 2, sb.length());
    return sb.toString();
  }

  private static Map<Integer, Double> getInvalidUserGroupTransactionSums() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "SELECT ug.id As ugid, SUM(CASE WHEN ug = t.fromUserGroup THEN -t.value ELSE t.value END) AS tSum, ug.value "
                + "FROM UserGroup ug INNER JOIN Transaction t ON ug IN (t.fromUserGroup, t.toUserGroup) "
                + "GROUP BY ug.id "
                + "HAVING ABS(ug.value - SUM(CASE WHEN ug = t.fromUserGroup THEN -t.value ELSE t.value END)) > 0.004",
            Tuple.class)
        .getResultStream()
        .collect(
            Collectors.toMap(
                tuple -> ((Integer) tuple.get("ugid")), tuple -> ((Double) tuple.get("tSum"))));
  }

  public static void checkUserGroupConsistency()
      throws InconsistentUserGroupValueException, MissingFullMemberException {
    if (!getInvalidUserGroupTransactionSums().isEmpty()) {
      throw new InconsistentUserGroupValueException();
    }
    ;
    for (UserGroup ug : UserGroup.getAll(null)) {
      User.validateGroupMemberships(
          ug.getMembers(),
          "Benutzergruppe(n) ohne Vollmitglied gefunden. Bitte den Vorstand informieren.");
    }
  }

  private static UserGroup getWithTransactionSum(int id, double sum) {
    UserGroup userGroup = getAll("where id = " + id).get(0);
    userGroup.transactionSum = sum;
    return userGroup;
  }

  public static List<UserGroup> getInconsistentUserGroups() {
    return getInvalidUserGroupTransactionSums().entrySet().stream()
        .map(e -> getWithTransactionSum(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.userGroupEquals(this);
  }
}
