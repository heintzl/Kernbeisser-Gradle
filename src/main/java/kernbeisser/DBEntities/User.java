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

    @JoinColumn
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> permissions = new HashSet<>();

    @Column
    private int shares;

    @Column
    private int solidaritySurcharge;

    @Column
    private String extraJobs;

    @JoinColumn
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Job> jobs = new HashSet<>();

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

    @Column
    private boolean unreadable = false;

    public static List<User> getAll(String condition) {
        return Tools.getAll(User.class, condition);
    }

    public static User getByUsername(String username) throws NoResultException{
        EntityManager em = DBConnection.getEntityManager();
        try{
            return em.createQuery("select u from User u where u.username = :username", User.class).setParameter("username",username).getSingleResult();
        }catch (NoResultException e){
            throw e;
        }finally {
            em.close();
        }
    }

    public static void makeUserUnreadable(User user){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        User dbContent = em.find(User.class,user.getId());
        dbContent.unreadable = true;
        dbContent.firstName = "deleted";
        dbContent.surname = "deleted";
        dbContent.username = "deleted"+dbContent.getId();
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
                "select u from User u where u.unreadable = false and (u.firstName like :search or u.surname like :search or u.username like :search) order by u.firstName ASC",
                User.class)
                                 .setParameter("search", s + "%")
                                 .setMaxResults(max)
                                 .getResultList();
        em.close();
        return out;
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

    public Collection<ValueChange> getAllValueChanges(){
        ArrayList<ValueChange> out = new ArrayList<>();
        EntityManager em = DBConnection.getEntityManager();
        out.addAll(em.createQuery("select t from Transaction t where t.from.id = :uid or t.to.id = :uid",Transaction.class).setParameter("uid",id).getResultList());
        out.addAll(em.createQuery("select p from Purchase p where p.session.customer.id = :uid",Purchase.class).setParameter("uid",id).getResultList());
        em.close();
        out.sort(Comparator.comparing(ValueChange::getDate));
        return out;
    }

    public Collection<Transaction> getAllTransactions(){
        EntityManager em = DBConnection.getEntityManager();
        Collection<Transaction> out = em.createQuery("select t from Transaction t where t.from.id = :uid or t.to.id = :uid",Transaction.class).setParameter("uid",id).getResultList();
        em.close();
        return out;
    }

    public Collection<Purchase> getAllPurchases(){
        EntityManager em = DBConnection.getEntityManager();
        Collection<Purchase> out = em.createQuery("select p from Purchase p where p.session.customer.id = :uid",Purchase.class).setParameter("uid",id).getResultList();
        em.close();
        return out;
    }
}