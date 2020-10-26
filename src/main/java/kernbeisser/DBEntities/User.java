package kernbeisser.DBEntities;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.IterableProtection.ProxyIterable;
import kernbeisser.Security.Key;
import kernbeisser.Security.PermissionSet;
import kernbeisser.Security.PermissionSetSecurityHandler;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table
@NoArgsConstructor
@EqualsAndHashCode(
    doNotUseGetters = true,
    exclude = {"ignoredDialogs"})
public class User implements Serializable {
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
        @ProxyIterable(
            read = {PermissionKey.USER_PERMISSIONS_READ},
            modify = {PermissionKey.USER_PERMISSIONS_WRITE})
      })
  private Set<Permission> permissions = new HashSet<>();

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SHARES_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SHARES_READ)})
  private int shares;

  @Column
  @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EXTRA_JOBS_WRITE)})
  @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EXTRA_JOBS_READ)})
  private String extraJobs;

  @JoinColumn
  @ManyToMany(fetch = FetchType.EAGER)
  @Getter(
      onMethod_ = {
        @ProxyIterable(
            read = {PermissionKey.USER_JOBS_READ},
            modify = {PermissionKey.USER_JOBS_WRITE})
      })
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
  private long townCode;

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

  public static List<User> getAll(String condition) {
    return Tools.getAll(User.class, condition);
  }

  public static User getByUsername(String username) throws NoResultException {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select u from User u where u.username = :username", User.class)
          .setParameter("username", username)
          .getSingleResult();
    } finally {
      em.close();
    }
  }

  @kernbeisser.Security.Key(PermissionKey.USER_GROUP_VALUE_READ)
  public double getRoundedValue() {
    return Math.round(userGroup.getValue() * 100) / 100.0;
  }

  public static void makeUserUnreadable(User user) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    User dbContent = em.find(User.class, user.getId());
    dbContent.unreadable = true;
    dbContent.firstName = "deleted";
    dbContent.surname = "deleted";
    dbContent.username = "deleted" + dbContent.id;
    dbContent.phoneNumber1 = "deleted";
    dbContent.phoneNumber2 = "deleted";
    dbContent.email = "deleted";
    dbContent.townCode = -1;
    dbContent.town = "deleted";
    dbContent.password = "";
    dbContent.street = "deleted";
    dbContent.permissions.clear();
    em.persist(dbContent);
    em.flush();
    et.commit();
  }

  public static Collection<User> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<User> out =
        em.createQuery(
                "select u from User u where u.unreadable = false and ((u.firstName like :search or u.surname like :search or u.username like :search)) order by u.firstName ASC",
                User.class)
            .setParameter("search", s + "%")
            .setMaxResults(max)
            .getResultList();
    em.close();
    return Proxy.getSecureInstances(out);
  }

  public static User getById(int parseInt) {
    return Proxy.getSecureInstance(DBConnection.getEntityManager().find(User.class, parseInt));
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
    return this.getFirstName() + " " + this.getSurname();
  }

  public String toString() {
    return Tools.decide(this::getUsername, "Benutzer[" + id + "]");
  }

  public boolean hasPermission(PermissionKey... keys) {
    for (PermissionKey key : keys) {
      boolean hasPermission = false;
      for (Permission permission : permissions) {
        if (permission.contains(key)) {
          hasPermission = true;
          break;
        }
      }
      if (!hasPermission) {
        return false;
      }
    }
    return true;
  }

  public boolean hasPermission(Collection<PermissionKey> keys) {
    for (PermissionKey key : keys) {
      boolean hasPermission = false;
      for (Permission permission : permissions) {
        if (permission.contains(key)) {
          hasPermission = true;
        }
      }
      if (!hasPermission) {
        return false;
      }
    }
    return true;
  }

  public Collection<Transaction> getAllValueChanges() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<Transaction> out =
        em.createQuery(
                "select t from Transaction t where t.from.id = :id or t.to.id = :id order by date",
                Transaction.class)
            .setParameter("id", id)
            .getResultList();
    em.close();
    return out;
  }

  public Collection<Transaction> getAllTransactions() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<Transaction> out =
        em.createQuery(
                "select t from Transaction t where t.from.id = :id or t.to.id = :id",
                Transaction.class)
            .setParameter("id", id)
            .getResultList();
    em.close();
    return out;
  }

  public Collection<Purchase> getAllPurchases() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    Collection<Purchase> out =
        em.createQuery("select p from Purchase p where p.session.customer.id = :id", Purchase.class)
            .setParameter("id", id)
            .getResultList();
    em.close();
    return out;
  }

  public static User getKernbeisserUser() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    try {
      return em.createQuery("select u from User u where u.username like 'kernbeisser'", User.class)
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
    } finally {
      em.close();
    }
  }

  public static User generateBeginnerUser() {
    User user = new User();
    user.permissions.add(PermissionConstants.BEGINNER.getPermission());
    PermissionSet set = new PermissionSet();
    set.loadKeys(PermissionKey.find(User.class));
    set.removePermission(PermissionKey.USER_PERMISSIONS_READ);
    set.removePermission(PermissionKey.USER_PERMISSIONS_WRITE);
    return Proxy.injectMethodHandler(user, new PermissionSetSecurityHandler(set));
  }

  public int getIdWithoutPermission() {
    return id;
  }

  public double valueAt(Instant instant) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
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
                        root.get("to").get("userGroup").get("id"), getUserGroup().getId()),
                    builder.equal(
                        root.get("from").get("userGroup").get("id"), getUserGroup().getId()))),
            builder.lessThan(root.get("date"), instant));
    double value = 0;
    for (Transaction transaction : em.createQuery(query).getResultList()) {
      if (transaction.getFrom().getUserGroup().getId() == getUserGroup().getId()) {
        value -= transaction.getValue();
      } else {
        value += transaction.getValue();
      }
    }
    return value;
  }

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
    return permissions.contains(PermissionConstants.BEGINNER.getPermission());
  }

  public void ignoreDialog(String name) {
    IgnoredDialog ignoredDialog = new IgnoredDialog(this, name);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    EntityTransaction et = em.getTransaction();
    et.begin();
    em.persist(ignoredDialog);
    em.flush();
    et.commit();
    getIgnoredDialogs().add(name);
  }
}
