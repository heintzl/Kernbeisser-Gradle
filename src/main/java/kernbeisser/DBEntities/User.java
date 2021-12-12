package kernbeisser.DBEntities;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Exeptions.InvalidValue;
import kernbeisser.Security.Key;
import kernbeisser.Security.Relations.UserRelated;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(
    indexes = {
      @Index(name = "IX_user_username", columnList = "username"),
      @Index(name = "IX_user_fullname", columnList = "firstName, surname", unique = true)
    })
@NoArgsConstructor
@EqualsAndHashCode(doNotUseGetters = true)
public class User implements Serializable, UserRelated {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false, insertable = false, nullable = false)
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_ID_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_ID_READ)})
  private int id;

  @JoinColumn
  @ManyToMany(fetch = FetchType.EAGER)
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PERMISSIONS_WRITE)})
  @Getter(
      onMethod_ = {
        @Key({PermissionKey.USER_PERMISSIONS_READ, PermissionKey.USER_PERMISSIONS_WRITE})
      })
  private Set<Permission> permissions = new HashSet<>();

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SHARES_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SHARES_READ)})
  private int shares;

  @JoinColumn
  @ManyToMany(fetch = FetchType.EAGER)
  @Getter(onMethod_ = {@Key({PermissionKey.USER_JOBS_READ, PermissionKey.USER_JOBS_WRITE})})
  @Setter(onMethod_ = {@Key({PermissionKey.USER_JOBS_WRITE})})
  private Set<Job> jobs = new HashSet<>();

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_KERNBEISSER_KEY_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_KERNBEISSER_KEY_READ)})
  private int kernbeisserKey;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EMPLOYEE_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EMPLOYEE_READ)})
  private boolean employee;

  @Column(unique = true, nullable = false)
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_USERNAME_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_USERNAME_READ)})
  private String username;

  @Column(nullable = false)
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PASSWORD_READ)})
  private String password;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_FIRST_NAME_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_FIRST_NAME_READ)})
  private String firstName;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SURNAME_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SURNAME_READ)})
  private String surname;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PHONE_NUMBER1_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PHONE_NUMBER1_READ)})
  private String phoneNumber1;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PHONE_NUMBER2_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PHONE_NUMBER2_READ)})
  private String phoneNumber2;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_STREET_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_STREET_READ)})
  private String street;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_TOWN_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_TOWN_READ)})
  private String town;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_TOWN_CODE_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_TOWN_CODE_READ)})
  private String townCode;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EMAIL_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EMAIL_READ)})
  private String email;

  @CreationTimestamp
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_CREATE_DATE_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_CREATE_DATE_READ)})
  private Instant createDate;

  @UpdateTimestamp
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_UPDATE_DATE_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_UPDATE_DATE_READ)})
  private Instant updateDate;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @JoinColumn(nullable = true)
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_UPDATE_BY_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_UPDATE_BY_READ)})
  private User updateBy;

  @ManyToOne
  @JoinColumn(nullable = false)
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_USER_GROUP_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_USER_GROUP_READ)})
  private UserGroup userGroup;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_UNREADABLE_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_UNREADABLE_READ)})
  private boolean unreadable = false;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_LAST_PASSWORD_CHANGE_READ)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_LAST_PASSWORD_CHANGE_WRITE)})
  private Instant lastPasswordChange;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_FORCE_PASSWORD_CHANGE_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_FORCE_PASSWORD_CHANGE_WRITE)})
  private boolean forcePasswordChange = false;

  @Column
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_ID_READ)})
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_ID_READ)})
  private boolean active = true;

  // primary only required during data import
  @Column @Transient @Getter @Setter private boolean primary;

  @Column @Transient private boolean setUpdatedBy = true;

  public User(String username) {
    this.username = username;
  }

  @PrePersist
  @PreUpdate
  private void setUpdateBy() {
    if (setUpdatedBy && LogInModel.getLoggedIn() != null) updateBy = LogInModel.getLoggedIn();
  }

  public static final String GENERIC_USERS_CONDITION =
      "upper(username) IN ('KERNBEISSER', 'ADMIN')";

  @Key(PermissionKey.USER_PERMISSIONS_READ)
  public Set<Permission> getPermissionsAsAvailable() {
    return Tools.or(this::getPermissions, Collections.unmodifiableSet(permissions));
  }

  @Key(PermissionKey.USER_JOBS_READ)
  public Set<Job> getJobsAsAvailable() {
    return Tools.or(this::getJobs, Collections.unmodifiableSet(jobs));
  }

  public static List<User> getAll(String condition) {
    return Tools.getAll(User.class, condition);
  }

  public static User getByUsername(String username) throws NoResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select u from User u where u.username = :username", User.class)
        .setParameter("username", username)
        .getSingleResult();
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

  @Key(PermissionKey.ACTION_ADD_BEGINNER)
  private void deleteBeginner() {}

  public boolean canDelete() {
    return Tools.canInvoke(this::deleteUser)
        || (isBeginner() && (Tools.canInvoke(this::deleteBeginner)));
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

  public static void refreshActivity() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    Collection<User> activeUsers =
        em.createQuery(
                "SELECT u FROM User u INNER JOIN Transaction t ON (u = t.fromUser and t.transactionType = :t_type) "
                    + "WHERE active = true GROUP BY u HAVING MAX(t.date) < :deadline",
                User.class)
            .setParameter("t_type", TransactionType.PURCHASE)
            .setParameter(
                "deadline",
                Instant.now().minus(Setting.DAYS_BEFORE_INACTIVITY.getIntValue(), ChronoUnit.DAYS))
            .getResultList();
    for (User u : activeUsers) {
      u.setUpdatedBy = false;
      u.active = false;
      em.persist(u);
    }
    em.flush();
  }

  public static Collection<User> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select u from User u where u.unreadable = false and lower(u.username) != 'admin' and ((u.firstName like :search or u.surname like :search or u.username like :search)) order by u.firstName ASC",
            User.class)
        .setParameter("search", s + "%")
        .setMaxResults(max)
        .getResultList();
  }

  /*  public boolean isActive() {
      Instant expireDate =
          Instant.now().minus(Setting.DAYS_BEFORE_INACTIVITY.getIntValue(), ChronoUnit.DAYS);
      return getAllPurchases().stream()
          .map(Purchase::getCreateDate)
          .max(Comparator.comparingLong(d -> d.getLong(ChronoField.INSTANT_SECONDS)))
          .orElse(Instant.MIN)
          .isAfter(expireDate);
    }
  */
  public static User getById(int parseInt) {
    return DBConnection.getEntityManager().find(User.class, parseInt);
  }

  @kernbeisser.Security.Key(PermissionKey.USER_PASSWORD_WRITE)
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

  public String getFullName(boolean firstSurname) {
    return firstSurname
        ? Tools.accessString(this::getSurname) + " " + Tools.accessString(this::getFirstName)
        : Tools.accessString(this::getFirstName) + " " + Tools.accessString(this::getSurname);
  }

  public String getJobsAsString() {
    return Job.concatenateJobs(getJobsAsAvailable());
  }

  public String toString() {
    return Tools.optional(this::getUsername).orElse("Benutzer[" + id + "]");
  }

  public Collection<User> getAllGroupMembers() {
    return getUserGroup().getMembers();
  }

  public Collection<Transaction> getAllValueChanges() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select t from Transaction t where t.fromUserGroup = :ug or t.toUserGroup = :ug order by date asc",
            Transaction.class)
        .setParameter("ug", getUserGroup())
        .getResultList();
  }

  public Collection<Purchase> getAllPurchases() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select p from Purchase p where p.session.customer.id = :id", Purchase.class)
        .setParameter("id", id)
        .getResultList();
  }

  public static void checkAdminConsistency() throws InvalidValue {

    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      String checkValue =
          em.createQuery(
                  "select case when exists"
                      + "(select t from Transaction t where u IN(fromUser, toUser) or u.userGroup IN (fromUserGroup, toUserGroup)) "
                      + "then 'invalid Transaction' "
                      + "when (select value from UserGroup ug where u.userGroup = ug) <> 0 then 'invalid value' "
                      + "else 'OK' end as result "
                      + "from User u "
                      + "where u.username = 'Admin'",
                  String.class)
              .getSingleResult();
      if (!checkValue.equals("OK")) {
        throw new InvalidValue("Admin user state error: " + checkValue);
      }
    } catch (Exception e) {
      Tools.showUnexpectedErrorWarning(e);
      throw new RuntimeException(e);
    }
  }

  public static User getKernbeisserUser() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      @Cleanup(value = "commit")
      EntityTransaction et = em.getTransaction();
      et.begin();
      return em.createQuery("select u from User u where u.username = 'kernbeisser'", User.class)
          .setMaxResults(1)
          .getSingleResult();
    } catch (NoResultException e) {
      EntityTransaction et = em.getTransaction();
      et.begin();
      User kernbeisser = new User();
      kernbeisser.getPermissions().add(PermissionConstants.APPLICATION.getPermission());
      kernbeisser.setPassword("CANNOT LOG IN");
      kernbeisser.setFirstName("Konto");
      kernbeisser.setSurname("Kernbeisser");
      kernbeisser.setUsername("kernbeisser");
      UserGroup kernbeisserValue = new UserGroup();
      em.persist(kernbeisserValue);
      kernbeisser.setUserGroup(kernbeisserValue);
      em.persist(kernbeisser);
      em.flush();
      et.commit();
      return getKernbeisserUser();
    }
  }

  public double valueAt(Instant instant) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    CriteriaBuilder builder = em.getCriteriaBuilder();
    builder.createQuery(Transaction.class);
    CriteriaQuery<Transaction> query = builder.createQuery(Transaction.class);
    Root<Transaction> root = query.from(Transaction.class);
    query
        .select(root)
        .where(
            builder.and(
                builder.or(
                    builder.equal(
                        root.get("toUser").get("userGroup").get("id"), getUserGroup().getId()),
                    builder.equal(
                        root.get("fromUser").get("userGroup").get("id"), getUserGroup().getId()))),
            builder.lessThan(root.get("date"), instant));
    double value = 0;
    for (Transaction transaction : em.createQuery(query).getResultList()) {
      if (transaction.getFromUser().getUserGroup().getId() == getUserGroup().getId()) {
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

  public boolean isBeginner() {
    return !getPermissionsAsAvailable().contains(PermissionConstants.FULL_MEMBER.getPermission());
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
        .map(Permission::getKeySet)
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

  public static Collection<User> getAllUserFullNames(boolean withKbUser) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    List<User> result =
        em.createQuery(
                "select u from User u where not "
                    + GENERIC_USERS_CONDITION
                    + " order by firstname,surName asc",
                User.class)
            .getResultList();
    if (withKbUser) result.add(0, User.getKernbeisserUser());
    return result;
  }

  public static Collection<User> getGenericUsers() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select u from User u where " + GENERIC_USERS_CONDITION, User.class)
        .getResultList();
  }

  public static void populateUserComboBox(
      AdvancedComboBox<User> box, boolean withKbUser, Predicate<User> filter) {
    Optional<User> selected = box.getSelected();
    List<User> boxItems = new ArrayList<>();
    if (withKbUser) {
      boxItems.add(User.getKernbeisserUser());
    }
    getAllUserFullNames(false).stream().filter(filter).forEach(boxItems::add);
    box.setItems(boxItems);
    if (box.isEnabled()) {
      if (selected.isPresent() && filter.test(selected.get())) {
        box.setSelectedItem(selected.get());
      } else {
        box.setSelectedItem(null);
      }
    }
  }

  @Override
  public boolean isInRelation(@NotNull User user) {
    return user.userGroup.equals(this.userGroup);
  }

  public boolean userGroupEquals(UserGroup userGroup) {
    return this.userGroup.equals(userGroup);
  }

  public boolean verifyPassword(char[] password) {
    return BCrypt.verifyer().verify(password, this.password.toCharArray()).verified;
  }
}
