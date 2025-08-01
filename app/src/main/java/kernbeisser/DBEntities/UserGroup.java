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
  @Setter
  @Transient
  private Double transactionSum;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_GROUP_SOLIDARITY_SURCHARGE_READ)})
  private double solidaritySurcharge;

  @Transient private Double oldSolidarity;

  public static void validateGroupMemberships(Collection<User> members, String exceptionMessage)
      throws MissingFullMemberException {
    if (members.size() > 1 && members.stream().noneMatch(User::isFullMember)) {
      throw new MissingFullMemberException(exceptionMessage);
    }
  }

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
        QueryBuilder.select(User_.userGroup.child(UserGroup_.id))
            .where(User_.unreadable.eq(false), User.GENERIC_USERS_PREDICATE.not())
            .getResultList(em);
    HashSet<Integer> ugIdSet = new HashSet<>(userGroupIds);
    return QueryBuilder.selectAll(UserGroup.class)
        .where(in(UserGroup_.id, ugIdSet))
        .getResultList(em);
  }

  public UserGroup withMembersAsStyledString(
      boolean withNames, Map<UserGroup, Double> overrideValues) {
    UserGroup result = new UserGroup();
    result.membersAsString =
        QueryBuilder.select(User.class, User_.id, User_.firstName, User_.surname, User.IS_FULL_USER)
            .where(User_.userGroup.eq(this))
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
    return QueryBuilder.selectAll(User.class).where(User_.userGroup.eq(this)).getResultList();
  }

  public static Collection<UserGroup> defaultSearch(String s, int i) {
    String userSearchPattern = s + "%";
    return QueryBuilder.select(User_.userGroup)
        .where(
            or(
                like(User_.username, userSearchPattern),
                like(User_.firstName, userSearchPattern),
                like(User_.surname, userSearchPattern)))
        .distinct()
        .getResultList();
  }

  public String getMemberString() {
    StringBuilder sb = new StringBuilder();
    for (Tuple tuple :
        QueryBuilder.select(User_.firstName, User_.surname)
            .where(User_.userGroup.eq(this))
            .getResultList()) {
      sb.append(User.getFullName(tuple.get(0, String.class), tuple.get(1, String.class), false))
          .append(", ");
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
    Map<Integer, Double> userGroupIdValueMap = getValueMapAt(em, dataOfLastTransaction);
    QueryBuilder.select(User_.userGroup.child(UserGroup_.id))
        .where(
            or(
                User.GENERIC_USERS_PREDICATE,
                and(User_.unreadable.eq(true), User_.unreadable.eq(!withUnreadables))))
        .getResultList(em)
        .forEach(userGroupIdValueMap::remove);
    return userGroupIdValueMap;
  }

  public static Map<Integer, Double> getValueMapAt(
      EntityManager em, Instant dateOfLastTransaction) {
    List<Tuple> transactionsUntilDate =
        QueryBuilder.select(
                Transaction_.fromUserGroup.child(UserGroup_.id),
                Transaction_.toUserGroup.child(UserGroup_.id),
                Transaction_.value)
            .where(
                PredicateFactory.lessOrEq(Transaction_.date, asExpression(dateOfLastTransaction)))
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
