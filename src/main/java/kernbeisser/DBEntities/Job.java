package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.time.Instant;
import java.util.Collection;
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
    private Instant createDate;

    @UpdateTimestamp
    private Instant updateDate;

    public static List<Job> getAll(String condition) {
        return Tools.getAll(Job.class, condition);
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public Instant getUpdateDate() {
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Job && ((Job) obj).getId() == this.getId();
    }

    public static Collection<Job> defaultSearch(String s, int max) {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Job> out = em.createQuery(
                "select j from Job j where j.name like :s or description like :sn",
                Job.class
        )
                                    .setParameter("s", s + "%")
                                    .setParameter("sn", "%"+s + "%")
                                    .setMaxResults(max)
                                    .getResultList();
        em.close();
        return out;
    }
}
