package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.UserGroup;
import lombok.Cleanup;

public class UserBalanceReport extends Report {
  final Timestamp timeStamp;
  private final boolean withNames;
  private List<UserGroup> userGroups;

  public UserBalanceReport(boolean withNames) {
    super(
        "userBalanceFileName",
        String.format(
            "KernbeisserGuthabenstände_%s",
            Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)).toString()));
    this.withNames = withNames;
    this.timeStamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
    this.userGroups = getUserGroups();
  }

  private List<UserGroup> getUserGroups() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select u from UserGroup u", UserGroup.class)
        .getResultStream()
        .filter(
            ug -> !ug.getMembers().stream().allMatch(u -> u.isUnreadable() || u.isKernbeisser()))
        .map(ug -> ug.withMembersAsString(this.withNames))
        .sorted((u1, u2) -> u1.getMembersAsString().compareToIgnoreCase(u2.getMembersAsString()))
        .collect(Collectors.toList());
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    double sum = 0;
    double sum_negative = 0;
    double sum_positive = 0;
    for (UserGroup ug : userGroups) {
      double value = ug.getValue();
      sum += value;
      if (value < 0) {
        sum_negative += value;
      } else {
        sum_positive += value;
      }
    }
    params.put("sum", sum);
    params.put("sum_negative", sum_negative);
    params.put("sum_positive", sum_positive);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return userGroups;
  }
}
