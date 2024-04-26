package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.User_;

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
    return QueryBuilder.selectAll(User.class)
        .where(User_.unreadable.eq(false))
        .getResultList()
        .stream()
        .filter(u -> !u.isUnreadable())
        .sorted(sorter)
        .collect(Collectors.toList());
  }
}
