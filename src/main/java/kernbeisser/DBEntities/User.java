package kernbeisser.DBEntities;


import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

@Entity
@Table
@NoArgsConstructor
@Where(clause = "unreadable = false")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, insertable = false, nullable = false)
    @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_ID_WRITE)})
    @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_ID_READ)})
    private int id;

    @JoinColumn
    @ManyToMany(fetch = FetchType.EAGER)
    @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PASSWORD_WRITE)})
    @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_PASSWORD_READ)})
    private Set<Permission> permissions = new HashSet<>();

    @Column
    @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SHARES_WRITE)})
    @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SHARES_READ)})
    private int shares;

    @Column
    @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SOLIDARITY_SURCHARGE_WRITE)})
    @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_SOLIDARITY_SURCHARGE_READ)})
    private double solidaritySurcharge;

    @Column
    @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EXTRA_JOBS_WRITE)})
    @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_EXTRA_JOBS_READ)})
    private String extraJobs;

    @JoinColumn
    @ManyToMany(fetch = FetchType.EAGER)
    @Setter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_JOBS_WRITE)})
    @Getter(onMethod_ = {@kernbeisser.Security.Key(PermissionKey.USER_JOBS_READ)})
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
        EntityManager em = DBConnection.getEntityManager();
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
        EntityManager em = DBConnection.getEntityManager();
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
        EntityManager em = DBConnection.getEntityManager();
        Collection<User> out = em.createQuery(
                "select u from User u where (u.firstName like :search or u.surname like :search or u.username like :search) order by u.firstName ASC",
                User.class)
                                 .setParameter("search", s + "%")
                                 .setMaxResults(max)
                                 .getResultList();
        em.close();
        return Proxy.getSecureInstances(out);
    }

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


    //changed from direct reference to getter to keep security
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
        EntityManager em = DBConnection.getEntityManager();
        Collection<Transaction> out = em.createQuery(
                "select t from Transaction t where t.from.id = :uid or t.to.id = :uid order by date",
                Transaction.class).setParameter("uid", id).getResultList();
        em.close();
        return out;
    }

    public Collection<Transaction> getAllTransactions() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Transaction> out = em.createQuery(
                "select t from Transaction t where t.from.id = :uid or t.to.id = :uid", Transaction.class)
                                        .setParameter("uid", id)
                                        .getResultList();
        em.close();
        return out;
    }

    public Collection<Purchase> getAllPurchases() {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Purchase> out = em.createQuery("select p from Purchase p where p.session.customer.id = :uid",
                                                  Purchase.class).setParameter("uid", id).getResultList();
        em.close();
        return out;
    }

    public static User getKernbeisserUser() {
        EntityManager em = DBConnection.getEntityManager();
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


    public User(User other) {
        this.id = other.id;
        this.permissions = other.permissions;
        this.shares = other.shares;
        this.solidaritySurcharge = other.solidaritySurcharge;
        this.extraJobs = other.extraJobs;
        this.jobs = other.jobs;
        this.kernbeisserKey = other.kernbeisserKey;
        this.employee = other.employee;
        this.username = other.username;
        this.password = other.password;
        this.firstName = other.firstName;
        this.surname = other.surname;
        this.phoneNumber1 = other.phoneNumber1;
        this.phoneNumber2 = other.phoneNumber2;
        this.street = other.street;
        this.town = other.town;
        this.townCode = other.townCode;
        this.email = other.email;
        this.createDate = other.createDate;
        this.updateDate = other.updateDate;
        this.userGroup = other.userGroup;
        this.unreadable = other.unreadable;
        this.lastPasswordChange = other.lastPasswordChange;
        this.forcePasswordChange = other.forcePasswordChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id &&
               shares == user.shares &&
               Double.compare(user.solidaritySurcharge, solidaritySurcharge) == 0 &&
               kernbeisserKey == user.kernbeisserKey &&
               employee == user.employee &&
               townCode == user.townCode &&
               unreadable == user.unreadable &&
               forcePasswordChange == user.forcePasswordChange &&
               Objects.equals(permissions, user.permissions) &&
               Objects.equals(extraJobs, user.extraJobs) &&
               Objects.equals(jobs, user.jobs) &&
               Objects.equals(username, user.username) &&
               Objects.equals(password, user.password) &&
               Objects.equals(firstName, user.firstName) &&
               Objects.equals(surname, user.surname) &&
               Objects.equals(phoneNumber1, user.phoneNumber1) &&
               Objects.equals(phoneNumber2, user.phoneNumber2) &&
               Objects.equals(street, user.street) &&
               Objects.equals(town, user.town) &&
               Objects.equals(email, user.email) &&
               Objects.equals(createDate, user.createDate) &&
               Objects.equals(updateDate, user.updateDate) &&
               Objects.equals(userGroup, user.userGroup) &&
               Objects.equals(lastPasswordChange, user.lastPasswordChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permissions, shares, solidaritySurcharge, extraJobs, jobs, kernbeisserKey, employee,
                            username, password, firstName, surname, phoneNumber1, phoneNumber2, street, town, townCode,
                            email, createDate, updateDate, userGroup, unreadable, lastPasswordChange,
                            forcePasswordChange);
    }

    public static User generateBeginnerUser() {
        User user = new User();
        user.permissions.add(PermissionConstants.BEGINNER.getPermission());
        return user;
    }

    public int getIdWithoutPermission() {
        return id;
    }
}
