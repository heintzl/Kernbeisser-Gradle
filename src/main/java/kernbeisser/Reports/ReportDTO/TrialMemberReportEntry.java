package kernbeisser.Reports.ReportDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Date;
import lombok.Cleanup;
import lombok.Getter;

public class TrialMemberReportEntry {

  @Getter private final User user;
  @Getter private final double sumPurchase;
  private final Optional<Instant> lastPurchase;

  public TrialMemberReportEntry(User user, double sumPurchase, Instant lastPurchase) {
    this.user = user;
    this.sumPurchase = sumPurchase;
    this.lastPurchase = Optional.ofNullable(lastPurchase);
  }

  public static List<TrialMemberReportEntry> getAllTrialMembers() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    String query =
        "SELECT new kernbeisser.Reports.ReportDTO.TrialMemberReportEntry(u, coalesce(SUM(t.value), 0), MAX(t.date)) "
            + "FROM User u LEFT JOIN Transaction t ON "
            + "EXISTS (SELECT s FROM SaleSession s WHERE s.transaction = t AND s.customer = u) "
            + "GROUP BY u";
    return em.createQuery(query, TrialMemberReportEntry.class)
        .getResultStream()
        .filter(e -> e.getUser().isTrialMember())
        .collect(Collectors.toList());
  }

  public String getLastPurchaseAsString() {
    return lastPurchase.map(i -> Date.INSTANT_DATE.format(i)).orElse("-");
  }
}
