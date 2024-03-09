package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Exeptions.InconsistentUserGroupValueException;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Security.Access.UserRelated;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;

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
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_UPDATE_DATE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_UPDATE_DATE_READ)})
  private Instant updateDate;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @JoinColumn(nullable = true)
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_UPDATE_BY_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_UPDATE_BY_READ)})
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
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_READ)})
  private double solidaritySurcharge;

  @Transient private Double oldSolidarity;

  @PostLoad
  private void rememberValues() {
    oldSolidarity = solidaritySurcharge;
  }

  @PreUpdate
  @PrePersist
  private void setUpdateBy() {
    if (Optional.ofNullable(oldSolidarity).filter(d -> d != solidaritySurcharge).isPresent()) {
      if (LogInModel.getLoggedIn() != null) updateBy = LogInModel.getLoggedIn();
    }
  }

  public static List<UserGroup> getActiveUserGroups() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select ug from UserGroup ug where ug in "
                + "(select u.userGroup from User u where u.unreadable = false and not "
                + User.GENERIC_USERS_CONDITION
                + ")",
            UserGroup.class)
        .getResultList();
  }

  public UserGroup withMembersAsStyledString(
      boolean withNames, Map<UserGroup, Double> overrideValues) {
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
    result.value = overrideValues.getOrDefault(this, getValue());
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

  public static Map<UserGroup, Double> getValueMapAtTransactionId(
      Long tId, boolean withUnreadables) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "SELECT ug AS ug, SUM(CASE WHEN ug = t.fromUserGroup THEN -t.value ELSE t.value END) AS tSum "
                + "FROM UserGroup ug INNER JOIN Transaction t ON ug IN (t.fromUserGroup, t.toUserGroup) "
                + "WHERE t.id <= :tid AND ug in (select u.userGroup from User u where (:all = true OR u.unreadable = false) and not "
                + User.GENERIC_USERS_CONDITION
                + ") "
                + "GROUP BY ug",
            Tuple.class)
        .setParameter("tid", tId)
        .setParameter("all", withUnreadables)
        .getResultStream()
        .collect(Collectors.toMap(t -> (UserGroup) t.get("ug"), t -> (Double) t.get("tSum")));
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

  public static Map<String, Object> getValueAggregatesAtTransactionId(long transactionId)
      throws NoResultException {
    return getValueAggregatesAtTransactionId(transactionId, true);
  }

  public static Map<String, Object> getValueAggregates() {
    return getValueAggregatesAtTransactionId(Long.MAX_VALUE, false);
  }

  private static Map<String, Object> getValueAggregatesAtTransactionId(
      Long tId, boolean withUnreadables) {
    Map<String, Object> params = new HashMap<>();
    double sum = 0;
    double sum_negative = 0;
    double sum_positive = 0;
    Map<UserGroup, Double> historicGroups = getValueMapAtTransactionId(tId, withUnreadables);
    for (UserGroup ug : historicGroups.keySet()) {
      double value = historicGroups.get(ug);
      sum += value;
      if (value < 0) {
        sum_negative += value;
      } else {
        sum_positive += value;
      }
    }
    params.put("sum", sum);
    params.put("sum_negative", sum_negative);
    params.put("sum_positive", sum_positive);
    return params;
  }

  public boolean containsUser(User u){
    return u.getUserGroup().id == id;
  }
  
  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.userGroupEquals(this);
  }
}
