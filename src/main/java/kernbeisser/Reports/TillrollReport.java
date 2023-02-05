package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ShoppingItem;
import lombok.Cleanup;

public class TillrollReport extends Report {
  private final Instant start;
  private final Instant endExclusive;

  public TillrollReport(Instant start, Instant endExclusive) {
    super(ReportFileNames.TILLROLL_REPORT_FILENAME);
    this.start = start;
    this.endExclusive = endExclusive;
  }

  @Override
  String createOutFileName() {
    return String.format(
        "KernbeisserBonrolle_%s_%s", Timestamp.from(start), Timestamp.from(endExclusive));
  }

  @Override
  Map<String, Object> getReportParams() {
    Timestamp startDate = Timestamp.from(start);
    Timestamp endDate = Timestamp.from(endExclusive.minus(1, ChronoUnit.MICROS));
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("start", startDate);
    reportParams.put("ende", endDate);
    return reportParams;
  }

  @Override
  public Collection<?> getDetailCollection() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select si from ShoppingItem si where not si.purchase.createDate < :start and purchase.createDate < :end",
            ShoppingItem.class)
        .setParameter("start", start)
        .setParameter("end", endExclusive)
        .getResultList();
  }
}
