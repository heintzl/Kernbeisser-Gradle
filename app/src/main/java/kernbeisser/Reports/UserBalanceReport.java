package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Repositories.TransactionRepository;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;

public class UserBalanceReport extends Report {
  final Timestamp timeStamp;
  private final boolean withNames;
  private List<UserGroup> userGroups;
  private final long reportNo;

  public UserBalanceReport(long reportNo, boolean withNames) {
    super(ReportFileNames.USER_BALANCE_REPORT_FILENAME);
    this.reportNo = reportNo;
    this.withNames = withNames;
    this.timeStamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
    this.userGroups = getUserGroups();
  }

  @Override
  String createOutFileName() {
    return String.format(
        "KernbeisserGuthabenstände_%s",
        (reportNo == -1
            ? Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)).toString()
            : reportNo));
  }

  private List<UserGroup> getUserGroups() {
    List<UserGroup> userGroups;
    final Map<UserGroup, Double> historicUserGroupValues =
        reportNo == -1
            ? new HashMap<>()
            : UserGroup.populateWithEntities(
                UserGroup.getValueMapAt(TransactionRepository.getLastOfReportNo(reportNo), true));
    if (reportNo == -1) {
      userGroups = UserGroup.getActiveUserGroups();
    } else {
      userGroups = new ArrayList<>(historicUserGroupValues.keySet());
    }
    return userGroups.stream()
        .filter(e -> !(e.getValue() == 0.0 && e.getMembers().stream().allMatch(User::isUnreadable)))
        .map(ug -> ug.withMembersAsStyledString(this.withNames, historicUserGroupValues))
        .sorted((u1, u2) -> u1.getMembersAsString().compareToIgnoreCase(u2.getMembersAsString()))
        .collect(Collectors.toList());
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params;
    if (reportNo == -1) {
      params = UserGroup.getValueAggregates();
    } else {
      params = UserGroup.getValueAggregatesAt(TransactionRepository.getLastOfReportNo(reportNo));
    }
    params.put(
        "reportTitle",
        "Guthabenstände" + (reportNo == -1 ? "" : " zu LD-Endabrechnung Nr. " + reportNo));
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return userGroups;
  }
}
