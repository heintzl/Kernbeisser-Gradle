package kernbeisser.DBEntities;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.DBConnection.PredicateFactory.*;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.TypeFields.TransactionField;
import kernbeisser.DBEntities.TypeFields.UserField;
import kernbeisser.DBEntities.TypeFields.UserGroupField;
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
    Collection<Integer> userGroupIds =
        QueryBuilder.select(UserField.userGroup.child(UserGroupField.id))
            .where(UserField.unreadable.eq(false), User.GENERIC_USERS_PREDICATE.not())
            .getResultList(em);
    HashSet<Integer> ugIdSet = new HashSet<>(userGroupIds);
    return QueryBuilder.selectAll(UserGroup.class)
        .where(in(UserGroupField.id, ugIdSet))
        .getResultList(em);
  }

  public UserGroup withMembersAsStyledString(
      boolean withNames, Map<UserGroup, Double> overrideValues) {
    UserGroup result = new UserGroup();
    result.membersAsString =
        QueryBuilder.select(
                User.class, UserField.id, UserField.firstName, UserField.surname, User.IS_FULL_USER)
            .where(UserField.userGroup.eq(this))
            .getResultList()
            .stream()
            .map(
                tuple ->
                    Tools.jasperTaggedStyling(
                        withNames
                            ? User.getFullName(
                                tuple.get(1, String.class), tuple.get(2, String.class), false)
                            : String.valueOf(tuple.get(0, Integer.class)),
                        tuple.get(3, Boolean.class) ? "" : "i"))
            .collect(Collectors.joining(", "));
    result.id = getId();
    result.value = overrideValues.getOrDefault(this, getValue());
    return result;
  }

  public Collection<User> getMembers() {
    return QueryBuilder.selectAll(User.class).where(UserField.userGroup.eq(this)).getResultList();
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

  public static Map<Integer, Double> getValueMapAt(
      Instant dataOfLastTransaction, boolean withUnreadables) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<Tuple> transactionsUntilDate =
        QueryBuilder.select(
                TransactionField.fromUserGroup.child(UserGroupField.id),
                TransactionField.toUserGroup.child(UserGroupField.id),
                TransactionField.value)
            .where(
                PredicateFactory.lessOrEq(
                    TransactionField.date, asExpression(dataOfLastTransaction)))
            .getResultList(em);
    Map<Integer, Double> userGroupIdValueMap = new HashMap<>(200);
    for (Tuple tuple : transactionsUntilDate) {
      Integer fromUserGroupId = tuple.get(0, Integer.class);
      Integer toUserGroupId = tuple.get(1, Integer.class);
      Double value = tuple.get(2, Double.class);
      Double oldValueFrom = userGroupIdValueMap.getOrDefault(fromUserGroupId, 0.0);
      Double oldValueTo = userGroupIdValueMap.getOrDefault(toUserGroupId, 0.0);
      userGroupIdValueMap.put(fromUserGroupId, oldValueFrom - value);
      userGroupIdValueMap.put(toUserGroupId, oldValueTo + value);
    }
    QueryBuilder.select(UserField.userGroup.child(UserGroupField.id))
        .where(
            or(
                User.GENERIC_USERS_PREDICATE,
                and(UserField.unreadable.eq(true), UserField.unreadable.eq(!withUnreadables))))
        .getResultList(em)
        .forEach(userGroupIdValueMap::remove);
    return userGroupIdValueMap;
  }

  // replaces id to V mapping with UserGroup to V mapping
  public static <V> Map<UserGroup, V> populateWithEntities(Map<Integer, V> map) {
    Map<Integer, UserGroup> ugIdMap =
        QueryBuilder.selectAll(UserGroup.class).getResultList().stream()
            .collect(Collectors.toMap(ug -> ug.id, ug -> ug));
    Map<UserGroup, V> resultMap = new HashMap<>(map.size());
    map.forEach((key, val) -> resultMap.put(ugIdMap.get(key), val));
    return resultMap;
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
    User.checkValidUserGroupMemberships();
  }

  private static UserGroup getWithTransactionSum(int id, double sum) {
    UserGroup userGroup =
        QueryBuilder.selectAll(UserGroup.class).where(UserGroupField.id.eq(id)).getSingleResult();
    userGroup.transactionSum = sum;
    return userGroup;
  }

  public static List<UserGroup> getInconsistentUserGroups() {
    return getInvalidUserGroupTransactionSums().entrySet().stream()
        .map(e -> getWithTransactionSum(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }

  public static Map<String, Object> getValueAggregatesAt(Instant transactionTimeStamp)
      throws NoResultException {
    return getValueAggregatesAt(transactionTimeStamp, true);
  }

  public static Map<String, Object> getValueAggregates() {
    return getValueAggregatesAt(Instant.now(), false);
  }

  private static Map<String, Object> getValueAggregatesAt(
      Instant transactionTimeStamp, boolean withUnreadables) {
    Map<String, Object> params = new HashMap<>();
    double sum = 0;
    double sum_negative = 0;
    double sum_positive = 0;
    Map<Integer, Double> historicGroups = getValueMapAt(transactionTimeStamp, withUnreadables);
    for (Double value : historicGroups.values()) {
      if (value < 0) sum_negative += value;
      else sum_positive += value;
      sum += value;
    }
    params.put("sum", sum);
    params.put("sum_negative", sum_negative);
    params.put("sum_positive", sum_positive);
    return params;
  }

  public boolean containsUser(User u) {
    return u.getUserGroup().id == id;
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.userGroupEquals(this);
  }
}
