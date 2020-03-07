package kernbeisser.DBEntities;


import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.Key;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.*;

@Entity
@Table
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, insertable = false, nullable = false)
    private int id;

    @Column
    private int salesThisYear;

    @JoinColumn
    @OneToMany
    private Set<Permission> permissions = new HashSet<>();

    @Column
    private int salesLastYear;

    @Column
    private int shares;

    @Column
    private int solidaritySurcharge;

    @Column
    private String extraJobs;

    @JoinColumn
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Job> jobs = new HashSet<>();

    @Column
    private Date lastBuy;

    @Column
    private boolean kernbeisserKey;

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
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserGroup userGroup;

    public static List<User> getAll(String condition) {
        return Tools.getAll(User.class, condition);
    }

    public static User getById(int id) {
        EntityManager em = DBConnection.getEntityManager();
        User out = em.createQuery("select u from User u where u.id = " + id, User.class).getSingleResult();
        em.close();
        return out;
    }

    public static Collection<User> defaultSearch(String s, int max) {
        EntityManager em = DBConnection.getEntityManager();
        Collection<User> out = em.createQuery(
                "select u from User u where u.firstName like :search or u.surname like :search or u.username like :search order by u.firstName ASC",
                User.class)
                                 .setParameter("search", s + "%")
                                 .setMaxResults(max)
                                 .getResultList();
        em.close();
        return out;
    }

    public int getSalesThisYear() {
        return salesThisYear;
    }

    public void setSalesThisYear(int salesThisYear) {
        this.salesThisYear = salesThisYear;
    }

    public int getSalesLastYear() {
        return salesLastYear;
    }

    public void setSalesLastYear(int salesLastYear) {
        this.salesLastYear = salesLastYear;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getSolidaritySurcharge() {
        return solidaritySurcharge;
    }

    public void setSolidaritySurcharge(int solidaritySurcharge) {
        this.solidaritySurcharge = solidaritySurcharge;
    }

    public String getExtraJobs() {
        return extraJobs;
    }

    public void setExtraJobs(String extraJobs) {
        this.extraJobs = extraJobs;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs.clear();
        this.jobs.addAll(jobs);
    }

    public Date getLastBuy() {
        return lastBuy;
    }

    public void setLastBuy(Date lastBuy) {
        this.lastBuy = lastBuy;
    }

    public boolean isKernbeisserKey() {
        return kernbeisserKey;
    }

    public void setKernbeisserKey(boolean kernbeisserKey) {
        this.kernbeisserKey = kernbeisserKey;
    }

    public boolean isEmployee() {
        return employee;
    }

    public void setEmployee(boolean employee) {
        this.employee = employee;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String address) {
        this.street = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public long getTownCode() {
        return townCode;
    }

    public void setTownCode(long townCode) {
        this.townCode = townCode;
    }

    public Collection<Transaction> getAllTransactions() {
        return Transaction.getAll("where from.id = " + id + " or to.id = " + id);
    }

    public Collection<Purchase> getAllPurchases() {
        return Purchase.getAll("where session.customer.id = " + id);
    }

    @Override
    public String toString() {
        return username;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

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
}