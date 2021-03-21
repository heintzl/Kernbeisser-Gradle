package kernbeisser.DBEntities;

import java.util.Collection;
import javax.persistence.*;
import kernbeisser.DBConnection.DBConnection;
import lombok.Cleanup;
import lombok.Getter;

@Entity
@Table
public class IgnoredDialog {

  public IgnoredDialog() {}

  public IgnoredDialog(User user, String origin) {
    this.user = user;
    this.origin = origin;
  }

  @Id @GeneratedValue private long id;
  @ManyToOne private User user;

  @Column @Getter private String origin;

  public static Collection<IgnoredDialog> getAllFor(User user) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select i from IgnoredDialog i where i.user = :u", IgnoredDialog.class)
        .setParameter("u", user)
        .getResultList();
  }
}
