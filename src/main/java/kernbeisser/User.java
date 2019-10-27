package kernbeisser;


import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, insertable = false, nullable = false)
    private int id;

    @Column
    private int salesThisYear;

    @Column
    private int salesLastYear;

    @Column
    private int interestThisYear;

    @Column
    private int shares;

    @Column
    private int solidaritySurcharge;

    @Column
    private String extraJobs;

    @JoinColumn
    @OneToMany
    private Set<Job> jobs = new HashSet<>(20);

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
    private String address;

    @Column
    private Permission permission = Permission.STANDARD;

    @Column
    private String email;

    @CreationTimestamp
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

    @ManyToOne
    @JoinColumn(nullable = false)
    private UserGroup userGroup;

    @ElementCollection
    private List<String> transferDate = new ArrayList<>(5);

    @ElementCollection
    private List<Integer> transferAmount = new ArrayList<>(5);

    public static String getJobName(int index) {
        switch (index) {
            case 0:
                return "Job1";
            case 1:
                return "Job2";
            case 2:
                return "Job3";
            case 3:
                return "Job4";
            case 4:
                return "Job5";
            case 5:
                return "Job6";
            case 6:
                return "Job7";
            case 7:
                return "Job8";
            case 8:
                return "Job9";
            case 9:
                return "Job10";
            case 10:
                return "Job11";
            case 11:
                return "Job12";
            case 12:
                return "Job13";
            case 13:
                return "Job14";
            case 14:
                return "Job15";
            case 15:
                return "Job16";
            case 16:
                return "Job17";
            case 17:
                return "Job18";
            case 18:
                return "Job19";
            case 19:
                return "Job20";
            default:
                return "NoName";
        }
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

    public int getInterestThisYear() {
        return interestThisYear;
    }

    public void setInterestThisYear(int interestThisYear) {
        this.interestThisYear = interestThisYear;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
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

    public List<String> getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(List<String> transferDate) {
        this.transferDate = transferDate;
    }

    public List<Integer> getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(List<Integer> transferAmount) {
        this.transferAmount = transferAmount;
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

}