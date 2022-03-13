package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.UserGroup;

public class UserBalanceReport extends Report {
  final Timestamp timeStamp;
  private final boolean withNames;
  private List<UserGroup> userGroups;
  private final long reportNo;

  public UserBalanceReport(long reportNo, boolean withNames) {
    super(
        "userBalanceFileName",
        String.format(
            "KernbeisserGuthabenstände_%s",
            Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)).toString()));
    this.reportNo = reportNo;
    this.withNames = withNames;
    this.timeStamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
    this.userGroups = getUserGroups();
  }

  private List<UserGroup> getUserGroups() {
    return UserGroup.getActiveUserGroups().stream()
        .map(ug -> ug.withMembersAsStyledString(this.withNames))
        .sorted((u1, u2) -> u1.getMembersAsString().compareToIgnoreCase(u2.getMembersAsString()))
        .collect(Collectors.toList());
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = UserGroup.getValueAggregates();
    params.put(
        "reportTitle",
        "Guthabenstände" + (reportNo == 0 ? "" : " zu LD-Endabrechnung Nr. " + reportNo));
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return userGroups;
  }
}
