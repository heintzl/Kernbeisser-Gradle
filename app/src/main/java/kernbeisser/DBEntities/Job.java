package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Useful.ActuallyCloneable;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import rs.groump.Key;
import rs.groump.PermissionKey;

@Entity
@Table
@EqualsAndHashCode(doNotUseGetters = true)
public class Job implements ActuallyCloneable {
  @Id
  @GeneratedValue
  @Getter(onMethod_ = {@Key(PermissionKey.JOB_ID_READ)})
  @Setter(onMethod_ = {@Key(PermissionKey.JOB_ID_WRITE)})
  private int id;

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

  public static Collection<Job> defaultSearch(String s, int max) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select j from Job j where j.name like :s or description like :sn", Job.class)
        .setParameter("s", s + "%")
        .setParameter("sn", "%" + s + "%")
        .setMaxResults(max)
        .getResultList();
  }

  public static String concatenateJobs(Set<Job> jobSet) {
    return jobSet.stream().map(Job::getName).collect(Collectors.joining(", "));
  }

  @Override
  public String toString() {
    return Tools.optional(this::getName).orElse("Job[" + id + "]");
  }

  @Override
  public Job clone() {
    try {
      return (Job) super.clone();
    } catch (CloneNotSupportedException e) {
      throw UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
    }
  }
}
