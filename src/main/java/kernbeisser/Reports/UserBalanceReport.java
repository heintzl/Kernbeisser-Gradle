package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
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

  public UserBalanceReport(boolean withNames) {
    super(
        "userBalanceFileName",
        String.format(
            "KernbeisserGuthabenstände_%s",
            Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES)).toString()));
    this.withNames = withNames;
    this.timeStamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery("select u from UserGroup u", UserGroup.class)
        .getResultStream()
        .map(ug -> ug.withMembersAsString(this.withNames))
        .sorted((u1, u2) -> u1.getMembersAsString().compareToIgnoreCase(u2.getMembersAsString()))
        .collect(Collectors.toList());
  }
}
