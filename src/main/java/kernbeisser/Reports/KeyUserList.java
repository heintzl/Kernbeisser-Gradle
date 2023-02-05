package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import lombok.Cleanup;

public class KeyUserList extends Report {

  private final String sortOrder;

  public KeyUserList(String sortOrder) {
    super(ReportFileNames.KEY_USER_LIST_REPORT_FILENAME);
    this.sortOrder = sortOrder;
  }

  @Override
  String createOutFileName() {
    return String.format(
        "Ladenbenutzerschlüssel_%s", Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)));
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    Comparator<User> sorter = Comparator.comparing(User::getId);
    if (Objects.equals(sortOrder, "Name")) {
      sorter = Comparator.comparing(User::getFullName);
    }
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select u from User u", User.class)
        .getResultStream()
        .filter(u -> !u.isUnreadable())
        .sorted(sorter)
        .collect(Collectors.toList());
  }
}
