package kernbeisser.DBEntities;

import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table
public class Job {
    @Id
    @GeneratedValue
    private int jid;

    @Column(unique = true)
    private String name;

    @Column
    private String description;

    @CreationTimestamp
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public int getId() {
        return jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static List<Job> getAll(String condition) {
        return Tools.getAll(Job.class, condition);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Job && ((Job) obj).getId() == this.getId();
    }
}
