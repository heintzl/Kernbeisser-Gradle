package kernbeisser.Reports;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Useful.Date;

public class UserBalanceReport extends Report {
  private final Instant date;
  private final boolean now;
  private final boolean withNames;
  private final List<UserGroup> userGroups;

  public UserBalanceReport(LocalDate date, boolean withNames) {
    super(ReportFileNames.USER_BALANCE_REPORT_FILENAME);
    if (date == null) {
      now = true;
      date = LocalDate.now();
    } else {
      now = false;
    }
    this.date = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant();
    this.withNames = withNames;
    this.userGroups = getUserGroups();
  }

  @Override
  String createOutFileName() {
    return String.format(
        "KernbeisserGuthabenstände_%s", Date.zonedDateFormat(date, Date.INSTANT_DATE));
  }

  private List<UserGroup> getUserGroups() {
    List<UserGroup> userGroups;
    final Map<UserGroup, Double> historicUserGroupValues =
        now ? new HashMap<>() : UserGroup.populateWithEntities(UserGroup.getValueMapAt(date, true));
    if (now) {
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
    if (now) {
      params = UserGroup.getValueAggregates();
    } else {
      params = UserGroup.getValueAggregatesAt(date);
    }
    params.put(
        "reportTitle",
        "Guthabenstände am %s"
            .formatted(
                now
                    ? Date.zonedDateFormat(Instant.now(), Date.INSTANT_DATE_TIME)
                    : Date.zonedDateFormat(date, Date.INSTANT_DATE)));
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return userGroups;
  }
}
