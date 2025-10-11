package kernbeisser.DBEntities;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.DBConnection.ExpressionFactory.upper;
import static kernbeisser.DBConnection.PredicateFactory.*;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBConnection.ExpressionFactory;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Exeptions.MissingFullMemberException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Security.Access.UserRelated;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Useful.Constants;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import rs.groump.Key;
import rs.groump.PermissionKey;
import rs.groump.PermissionSet;

@Entity
@Table(
    indexes = {
      @Index(name = "IX_user_username", columnList = "username"),
      @Index(name = "IX_user_fullname", columnList = "firstName, surname", unique = true)
    })
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
public class User implements Serializable, UserRelated, ActuallyCloneable {
  public static final PredicateFactory<User> GENERIC_USERS_PREDICATE =
      in(upper(User_.username), "KERNBEISSER", "ADMIN");

  public static final PredicateFactory<User> IS_FULL_USER =
      isMember(asExpression(PermissionConstants.FULL_MEMBER.getPermission()), User_.permissions);
  public static final PredicateFactory<User> IS_TRAIL_USER =
      isMember(asExpression(PermissionConstants.TRIAL_MEMBER.getPermission()), User_.permissions);
  public static final String GENERIC_USERS_CONDITION =
      "upper(username) IN ('KERNBEISSER', 'ADMIN')";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false, insertable = false, nullable = false)
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_ID_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_ID_READ)})
  private int id;

  @ManyToMany(fetch = FetchType.EAGER)
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_PERMISSIONS_WRITE)})
  @Getter(
      onMethod_ = {
        @Key({PermissionKey.USER_PERMISSIONS_READ, PermissionKey.USER_PERMISSIONS_WRITE})
      })
  private Set<Permission> permissions = new HashSet<>();

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_SHARES_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_SHARES_READ)})
  private int shares;

  @ManyToMany(fetch = FetchType.EAGER)
  @Getter(onMethod_ = {@Key({PermissionKey.USER_JOBS_READ, PermissionKey.USER_JOBS_WRITE})})
  @Setter(onMethod_ = {@Key({PermissionKey.USER_JOBS_WRITE})})
  private Set<Job> jobs = new HashSet<>();

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_KERNBEISSER_KEY_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_KERNBEISSER_KEY_READ)})
  private int kernbeisserKey;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_EMPLOYEE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_EMPLOYEE_READ)})
  private boolean employee;

  @Column(unique = true, nullable = false)
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_USERNAME_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_USERNAME_READ)})
  private String username;

  @Column(nullable = false)
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_PASSWORD_READ)})
  private String password;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_FIRST_NAME_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_FIRST_NAME_READ)})
  private String firstName;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_SURNAME_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_SURNAME_READ)})
  private String surname;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_PHONE_NUMBER1_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_PHONE_NUMBER1_READ)})
  private String phoneNumber1;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_PHONE_NUMBER2_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_PHONE_NUMBER2_READ)})
  private String phoneNumber2;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_STREET_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_STREET_READ)})
  private String street;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_TOWN_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_TOWN_READ)})
  private String town;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_TOWN_CODE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_TOWN_CODE_READ)})
  private String townCode;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_EMAIL_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_EMAIL_READ)})
  private String email;

  @CreationTimestamp
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_CREATE_DATE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_CREATE_DATE_READ)})
  private Instant createDate;

  @UpdateTimestamp
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_UPDATE_DATE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_UPDATE_DATE_READ)})
  private Instant updateDate;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @JoinColumn(nullable = true)
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_UPDATE_BY_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_UPDATE_BY_READ)})
  private User updateBy;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_USER_GROUP_READ)})
  private UserGroup userGroup;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_UNREADABLE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_UNREADABLE_READ)})
  private boolean unreadable = false;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_LAST_PASSWORD_CHANGE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_LAST_PASSWORD_CHANGE_READ)})
  private Instant lastPasswordChange;

  @Column
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_FORCE_PASSWORD_CHANGE_WRITE)})
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_FORCE_PASSWORD_CHANGE_READ)})
  private boolean forcePasswordChange = false;

  @Column
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_ID_READ)})
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_ID_READ)})
  private boolean active = true;

  @Column
  @Getter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_ID_READ)})
  @Setter(onMethod_ = {@rs.groump.Key(PermissionKey.USER_ID_READ)})
  private boolean testOnly = false;

  // primary only required during data import
  @Column @Transient @Getter @Setter private boolean primary;

  @Column @Transient @Setter @Getter private boolean setUpdatedBy = true;

  public User(String username) {
    this.username = username;
  }

  public static User getByUsername(String username) throws NoResultException {
    return QueryBuilder.getByProperty(User_.username, username);
  }

  public static Collection<User> defaultSearch(String s, int max) {
    String searchPattern = s + "%";
    return QueryBuilder.selectAll(User.class)
        .where(
            User_.unreadable.eq(false),
            GENERIC_USERS_PREDICATE.not(),
            or(
                like(User_.firstName, searchPattern),
                like(User_.surname, searchPattern),
                like(User_.username, searchPattern)))
        .orderBy(User_.firstName.asc())
        .limit(max)
        .getResultList();
  }

  public static User getById(int parseInt) {
    return QueryBuilder.selectAll(User.class).where(User_.id.eq(parseInt)).getSingleResult();
  }

  public boolean isShopUser() {
    return this.getId() == Constants.SHOP_USER_ID;
  }

  public static User getShopUser() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      return QueryBuilder.selectAll(User.class)
          .where(User_.username.eq("kernbeisser"))
          .getSingleResult(em);
    } catch (NoResultException e) {
      EntityTransaction et = em.getTransaction();
      et.begin();
      User kernbeisser = new User();
      kernbeisser.getPermissions().add(PermissionConstants.APPLICATION.getPermission());
      kernbeisser.setPassword("CANNOT LOG IN");
      kernbeisser.setFirstName("Konto");
      kernbeisser.setSurname("Kernbeisser");
      kernbeisser.setUsername("kernbeisser");
      kernbeisser.setNewUserGroup(em);
      em.persist(kernbeisser);
      em.flush();
      et.commit();
      return getShopUser();
    }
  }

  public static List<User> getAllUserFullNames(boolean withKbUser, boolean orderSurname) {
    var qb =
        QueryBuilder.selectAll(User.class)
            .where(User_.unreadable.eq(false), GENERIC_USERS_PREDICATE.not());
    if (orderSurname) {
      qb.orderBy(User_.surname.asc(), User_.firstName.asc());
    } else {
      qb.orderBy(User_.firstName.asc(), User_.surname.asc());
    }
    var result = qb.getResultList();
    if (withKbUser) result.addFirst(getShopUser());
    return result;
  }

  public static Collection<User> getGenericUsers() {
    return QueryBuilder.selectAll(User.class).where(GENERIC_USERS_PREDICATE).getResultList();
  }

  @PrePersist
  @PreUpdate
  private void setUpdateBy() {
    if (setUpdatedBy && LogInModel.getLoggedIn() != null) updateBy = LogInModel.getLoggedIn();
  }

  public static void validateGroupMemberships(User user, String exceptionMessage)
      throws MissingFullMemberException {
    UserGroup.validateGroupMemberships(user.getAllGroupMembers(), exceptionMessage);
  }

  @Key(PermissionKey.USER_USER_GROUP_WRITE)
  public void setNewUserGroup(EntityManager em) {
    em.persist(this.setNewUserGroup());
  }

  @Key(PermissionKey.USER_USER_GROUP_WRITE)
  public UserGroup setNewUserGroup() {
    UserGroup newUserGroup = new UserGroup();
    this.userGroup = newUserGroup;
    return newUserGroup;
  }

  @Key(PermissionKey.USER_USER_GROUP_WRITE)
  public void setUserGroup(UserGroup userGroup) throws MissingFullMemberException {

    if (isFullMember()) {
      if (this.userGroup != null) {
        Collection<User> remainingMembers = this.userGroup.getMembers();
        remainingMembers.remove(this);
        UserGroup.validateGroupMemberships(
            remainingMembers,
            "In der alten Benutzergruppe muss mindestens ein Vollmitglied bleiben");
      }
    } else {
      Collection<User> newMembers = userGroup.getMembers();
      newMembers.add(this);
      UserGroup.validateGroupMemberships(
          newMembers, "In der neuen Benutzergruppe muss mindestens ein Vollmitglied sein");
    }
    this.userGroup = userGroup;
  }

  @Key(PermissionKey.USER_PERMISSIONS_READ)
  public Set<Permission> getPermissionsAsAvailable() {
    return Tools.or(this::getPermissions, Collections.unmodifiableSet(permissions));
  }

  @Key(PermissionKey.USER_JOBS_READ)
  public Set<Job> getJobsAsAvailable() {
    return Tools.or(this::getJobs, Collections.unmodifiableSet(jobs));
  }

  private void makeUserUnreadable() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    User dbContent = em.find(User.class, getId());
    dbContent.unreadable = true;
    dbContent.firstName = "deleted";
    dbContent.surname = "deleted" + dbContent.id;
    dbContent.username = "deleted" + dbContent.id;
    dbContent.phoneNumber1 = "deleted";
    dbContent.phoneNumber2 = "deleted";
    dbContent.email = "deleted";
    dbContent.townCode = "deleted";
    dbContent.town = "deleted";
    dbContent.password = "";
    dbContent.street = "deleted";
    dbContent.permissions.clear();
    em.persist(dbContent);
    em.flush();
  }

  @Key(PermissionKey.REMOVE_USER)
  private void deleteUser() {}

  @Key(PermissionKey.ACTION_ADD_TRIAL_MEMBER)
  private void deleteTrialMember() {}

  public boolean canDelete() {
    return Tools.canInvoke(this::deleteUser)
        || (isTrialMember() && (Tools.canInvoke(this::deleteTrialMember)));
  }

  public boolean delete() {
    if (!canDelete()) {
      return false;
    } else {
      try {
        Tools.delete(this);
      } catch (PersistenceException e) {
        makeUserUnreadable();
      }
      return true;
    }
  }

  @rs.groump.Key(PermissionKey.USER_PASSWORD_WRITE)
  public void setPassword(String password) {
    if (!password.equals(this.password)) {
      this.password = password;
      this.lastPasswordChange = Instant.now();
      this.forcePasswordChange = false;
    }
  }

  // changed from direct reference to getter to keep security
  public String getFullName() {
    return getFullName(false);
  }

  public String getReverseFullName() {
    return getFullName(true);
  }

  public String getFullName(boolean firstSurname) {
    if (isKernbeisser()) {
      return surname;
    }
    return getFullName(
        Tools.accessString(this::getFirstName), Tools.accessString(this::getSurname), firstSurname);
  }

  public static String getFullName(String firstName, String surname, boolean firstSurname) {
    return firstSurname ? surname + ", " + firstName : firstName + " " + surname;
  }

  public String getJobsAsString() {
    return Job.concatenateJobs(getJobsAsAvailable());
  }

  public String toString() {
    return Tools.runIfPossible(this::getUsername).orElse("Benutzer[" + id + "]");
  }

  public Collection<User> getAllGroupMembers() {
    return getUserGroup().getMembers();
  }

  public Collection<Transaction> getAllValueChanges() {
    return QueryBuilder.selectAll(Transaction.class)
        .where(
            or(
                Transaction_.fromUserGroup.eq(getUserGroup()),
                Transaction_.toUserGroup.eq(getUserGroup())))
        .orderBy(Transaction_.date.asc())
        .getResultList();
  }

  public Collection<Purchase> getAllPurchases() {
    return QueryBuilder.selectAll(Purchase.class)
        .where(Purchase_.session.child(SaleSession_.customer.child(User_.userGroup)).eq(userGroup))
        .getResultList();
  }

  public double valueAt(Instant instant) {
    UserGroup ug = getUserGroup();
    List<Transaction> result =
        QueryBuilder.selectAll(Transaction.class)
            .where(
                or(Transaction_.toUserGroup.eq(ug), Transaction_.fromUserGroup.eq(ug)),
                lessOrEq(Transaction_.date, ExpressionFactory.asExpression(instant)))
            .getResultList();
    double value = 0;
    for (Transaction transaction : result) {
      if (transaction.getFromUserGroup().getId() == getUserGroup().getId()) {
        value -= transaction.getValue();
      } else {
        value += transaction.getValue();
      }
    }
    return value;
  }

  @EqualsAndHashCode.Exclude
  @Getter(lazy = true)
  @Transient
  private final Set<String> ignoredDialogs = loadDialogs();

  public boolean isIgnoredDialog(String dialogName) {
    return getIgnoredDialogs().contains(dialogName);
  }

  private Set<String> loadDialogs() {
    return new HashSet<>(Tools.transform(IgnoredDialog.getAllFor(this), IgnoredDialog::getOrigin));
  }

  public boolean isTrialMember() {
    return getPermissionsAsAvailable().contains(PermissionConstants.TRIAL_MEMBER.getPermission());
  }

  public boolean isFullMember() {
    return getPermissionsAsAvailable().contains(PermissionConstants.FULL_MEMBER.getPermission());
  }

  public boolean isKernbeisser() {
    return username.equals("kernbeisser");
  }

  public boolean isSysAdmin() {
    return username.equals("Admin");
  }

  public boolean mayGoUnderMin() {
    return getAllGroupMembers().stream()
        .map(User::getPermissionsAsAvailable)
        .flatMap(Collection::stream)
        .map(Permission::getKeySetAsAvailable)
        .flatMap(Collection::stream)
        .anyMatch(PermissionKey.GO_UNDER_MIN::equals);
  }

  public void ignoreDialog(String name) {
    IgnoredDialog ignoredDialog = new IgnoredDialog(this, name);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(ignoredDialog);
    em.flush();
    getIgnoredDialogs().add(name);
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.id == this.id || user.userGroup.equals(this.userGroup);
  }

  public boolean userGroupEquals(UserGroup userGroup) {
    return this.userGroup.equals(userGroup);
  }

  public boolean verifyPassword(char[] password) {
    return BCrypt.verifyer().verify(password, this.password.toCharArray()).verified;
  }

  public PermissionSet getPermissionSet() {
    PermissionSet permissionKeys = new PermissionSet();
    for (Permission permission : permissions) {
      permissionKeys.addAll(permission.getKeySet());
    }
    return permissionKeys;
  }

  @Override
  public User clone() {
    try {
      return (User) super.clone();
    } catch (CloneNotSupportedException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }
}
