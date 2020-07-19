package kernbeisser.DBEntities;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table
public class Job {
    @Id
    @GeneratedValue
    @Getter(onMethod_ = {@Key(PermissionKey.JOB_JID_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.JOB_JID_WRITE)})
    private int jid;

    @Column(unique = true)
    @Getter(onMethod_ = {@Key(PermissionKey.JOB_NAME_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.JOB_NAME_WRITE)})
    private String name;

    @Column
    @Getter(onMethod_ = {@Key(PermissionKey.JOB_DESCRIPTION_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.JOB_DESCRIPTION_WRITE)})
    private String description;

    @CreationTimestamp
    @Getter(onMethod_ = {@Key(PermissionKey.JOB_CREATE_DATE_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.JOB_CREATE_DATE_WRITE)})
    private Instant createDate;

    @UpdateTimestamp
    @Getter(onMethod_ = {@Key(PermissionKey.JOB_UPDATE_DATE_READ)})
    @Setter(onMethod_ = {@Key(PermissionKey.JOB_UPDATE_DATE_WRITE)})
    private Instant updateDate;

    public static List<Job> getAll(String condition) {
        return Tools.getAll(Job.class, condition);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Job && ((Job) obj).getJid() == this.getJid();
    }

    public static Collection<Job> defaultSearch(String s, int max) {
        EntityManager em = DBConnection.getEntityManager();
        Collection<Job> out = em.createQuery(
                "select j from Job j where j.name like :s or description like :sn",
                Job.class
        )
                                .setParameter("s", s + "%")
                                .setParameter("sn", "%" + s + "%")
                                .setMaxResults(max)
                                .getResultList();
        em.close();
        return out;
    }

    @Override
    public String toString() {
        return Tools.decide(this::getName, "Job[" + getJid() + "]");
    }


}
