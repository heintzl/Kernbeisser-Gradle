package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBEntities.ShoppingItem;

public class TillrollReport extends Report {

  private final Collection<ShoppingItem> tillroll;
  private final Instant start;
  private final Instant endExclusive;

  public TillrollReport(Collection<ShoppingItem> tillroll, Instant start, Instant endExclusive) {
    super(
        "tillrollFileName",
        String.format(
            "KernbeisserBonrolle_%s_%s",
            Timestamp.from(start).toString(), Timestamp.from(endExclusive).toString()));
    this.tillroll = tillroll;
    this.start = start;
    this.endExclusive = endExclusive;
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
  Collection<?> getDetailCollection() {
    return tillroll;
  }
}
