package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import lombok.Cleanup;

public class KeyUserList extends Report {
  private final String sortOrder;

  public KeyUserList(String sortOrder) {
    super(
        "keyUserListFileName",
        String.format(
            "Ladenbenutzerschlüssel_%s",
            Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES))));

    this.sortOrder = sortOrder;
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    Comparator<User> sorter = Comparator.comparing(User::getId);
    if (sortOrder == "Name") {
      sorter = Comparator.comparing(User::getFullName);
    }
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery("select u from User u", User.class)
        .getResultStream()
        .sorted(sorter)
        .collect(Collectors.toList());
  }
}
