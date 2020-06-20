package kernbeisser.DBEntities;


import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Key;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Security.Proxy;
import kernbeisser.Useful.Tools;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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
    private int id;

    @JoinColumn
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> permissions = new HashSet<>();

    @Column
    private int shares;

    @Column
    private double solidaritySurcharge;

    @Column
    private String extraJobs;

    @JoinColumn
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Job> jobs = new HashSet<>();

    @Column
    private int kernbeisserKey;

    @Column
    private boolean employee;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String firstName;

    @Column
    private String surname;

    @Column
    private String phoneNumber1;

    @Column
    private String phoneNumber2;

    @Column
    private String street;

    @Column
    private String town;

    @Column
    private long townCode;

    @Column
    private String email;

    @CreationTimestamp
    private Instant createDate;

    @UpdateTimestamp
    private Instant updateDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserGroup userGroup;

    @Column
    private boolean unreadable = false;

    @Column
    private Instant lastPasswordChange;

    @Column
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
        return DBConnection.getEntityManager().find(User.class,parseInt);
    }

    @kernbeisser.Security.Key(Key.USER_SHARES_READ)
    public int getShares() {
        return shares;
    }

    @kernbeisser.Security.Key(Key.USER_SHARES_WRITE)
    public void setShares(int shares) {
        this.shares = shares;
    }

    @kernbeisser.Security.Key(Key.USER_SOLIDARITY_SURCHARGE_READ)
    public double getSolidaritySurcharge() {
        return solidaritySurcharge;
    }

    @kernbeisser.Security.Key(Key.USER_SOLIDARITY_SURCHARGE_WRITE)
    public void setSolidaritySurcharge(double solidaritySurcharge) {
        this.solidaritySurcharge = solidaritySurcharge;
    }

    @kernbeisser.Security.Key(Key.USER_EXTRA_JOBS_READ)
    public String getExtraJobs() {
        return extraJobs;
    }

    @kernbeisser.Security.Key(Key.USER_EXTRA_JOBS_WRITE)
    public void setExtraJobs(String extraJobs) {
        this.extraJobs = extraJobs;
    }

    @kernbeisser.Security.Key(Key.USER_JOBS_READ)
    public Set<Job> getJobs() {
        return jobs;
    }

    @kernbeisser.Security.Key(Key.USER_JOBS_WRITE)
    public void setJobs(Set<Job> jobs) {
        this.jobs.clear();
        this.jobs.addAll(jobs);
    }

    @kernbeisser.Security.Key(Key.USER_KERNBEISSER_KEY_READ)
    public int getKernbeisserKeyNumber() {
        return kernbeisserKey;
    }

    @kernbeisser.Security.Key(Key.USER_KERNBEISSER_KEY_WRITE)
    public void setKernbeisserKey(int kernbeisserKey) {
        this.kernbeisserKey = kernbeisserKey;
    }

    @kernbeisser.Security.Key(Key.USER_EMPLOYEE_READ)
    public boolean isEmployee() {
        return employee;
    }

    @kernbeisser.Security.Key(Key.USER_EMPLOYEE_WRITE)
    public void setEmployee(boolean employee) {
        this.employee = employee;
    }

    @kernbeisser.Security.Key(Key.USER_ID_READ)
    public int getId() {
        return id;
    }

    @kernbeisser.Security.Key(Key.USER_USERNAME_READ)
    public String getUsername() {
        return username;
    }

    @kernbeisser.Security.Key(Key.USER_USERNAME_WRITE)
    public void setUsername(String username) {
        this.username = username;
    }

    @kernbeisser.Security.Key(Key.USER_PASSWORD_READ)
    public String getPassword()throws AccessDeniedException {
        return password;
    }

    @kernbeisser.Security.Key(Key.USER_PASSWORD_WRITE)
    public void setPassword(String password) {
        if(!password.equals(this.password)) {
            this.password = password;
            this.lastPasswordChange = Instant.now();
            this.forcePasswordChange = false;
        }
    }


    @kernbeisser.Security.Key(Key.USER_FIRST_NAME_READ)
    public String getFirstName() {
        return firstName;
    }

    @kernbeisser.Security.Key(Key.USER_FIRST_NAME_WRITE)
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @kernbeisser.Security.Key(Key.USER_SURNAME_READ)
    public String getSurname() {
        return surname;
    }

    @kernbeisser.Security.Key(Key.USER_SURNAME_WRITE)
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @kernbeisser.Security.Key(Key.USER_PHONE_NUMBER1_READ)
    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    @kernbeisser.Security.Key(Key.USER_PHONE_NUMBER1_WRITE)
    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    @kernbeisser.Security.Key(Key.USER_PHONE_NUMBER2_READ)
    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    @kernbeisser.Security.Key(Key.USER_PHONE_NUMBER2_WRITE)
    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    @kernbeisser.Security.Key(Key.USER_STREET_READ)
    public String getStreet() {
        return street;
    }

    @kernbeisser.Security.Key(Key.USER_STREET_WRITE)
    public void setStreet(String address) {
        this.street = address;
    }

    @kernbeisser.Security.Key(Key.USER_EMAIL_READ)
    public String getEmail() {
        return email;
    }

    @kernbeisser.Security.Key(Key.USER_EMAIL_WRITE)
    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    @kernbeisser.Security.Key(Key.USER_USER_GROUP_READ)
    public UserGroup getUserGroup() {
        return userGroup;
    }

    @kernbeisser.Security.Key(Key.USER_USER_GROUP_WRITE)
    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    @kernbeisser.Security.Key(Key.USER_TOWN_READ)
    public String getTown() {
        return town;
    }

    @kernbeisser.Security.Key(Key.USER_TOWN_WRITE)
    public void setTown(String town) {
        this.town = town;
    }

    @kernbeisser.Security.Key(Key.USER_TOWN_CODE_READ)
    public long getTownCode() {
        return townCode;
    }
    @kernbeisser.Security.Key(Key.USER_TOWN_CODE_WRITE)
    public void setTownCode(long townCode) {
        this.townCode = townCode;
    }


    //changed from direct reference to getter to keep security
    public String getFullName() {
        return this.getFirstName() + " " + this.getSurname();
    }

    public String toString() {
        try {
            return getUsername();
            //catch AccessDeniedException
        }catch (Exception e){
            return "Benutzer["+id+"]";
        }
    }

    @kernbeisser.Security.Key(Key.USER_PERMISSION_READ)
    public Set<Permission> getPermissions() {
        return permissions;
    }

    @kernbeisser.Security.Key(Key.USER_PERMISSION_WRITE)
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Key... keys) {
        for (Key key : keys) {
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

    public boolean hasPermission(Collection<Key> keys) {
        for (Key key : keys) {
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
        Collection<Transaction> out = em.createQuery("select t from Transaction t where t.from.id = :uid or t.to.id = :uid order by date",
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

    public static User getKernbeisserUser(){
        EntityManager em = DBConnection.getEntityManager();
        try {
            return em.createQuery("select u from User u where u.username like 'kernbeisser'",User.class)
                                 .setMaxResults(1)
                                 .getSingleResult();
        }catch (NoResultException e){
            EntityTransaction et = em.getTransaction();
            et.begin();
            User kernbeisser = new User();
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
        }finally {
            em.close();
        }
    }

    public boolean isForcePasswordChange() {
        return forcePasswordChange;
    }

    public void setForcePasswordChange(boolean requiresPasswordChange) {
        this.forcePasswordChange = requiresPasswordChange;
    }

    public Instant getLastPasswordChange() {
        return lastPasswordChange;
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
}