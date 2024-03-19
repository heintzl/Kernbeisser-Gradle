package kernbeisser.DBEntities;

import jakarta.persistence.*;
import java.util.Collection;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Types.IgnoredDialogField;
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
    return QueryBuilder.queryTable(IgnoredDialog.class)
        .where(IgnoredDialogField.user.eq(user))
        .getResultList();
  }
}
