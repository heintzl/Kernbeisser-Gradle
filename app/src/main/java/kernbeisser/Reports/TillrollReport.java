package kernbeisser.Reports;

import static kernbeisser.DBConnection.ExpressionFactory.asExpression;
import static kernbeisser.DBConnection.PredicateFactory.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.Purchase_;
import kernbeisser.DBEntities.ShoppingItem_;

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
    return QueryBuilder.selectAll(ShoppingItem.class)
        .where(
            greaterOrEq(
                ShoppingItem_.purchase.child(Purchase_.createDate), asExpression(start)),
            lessThan(
                ShoppingItem_.purchase.child(Purchase_.createDate),
                asExpression(endExclusive)))
        .getResultList();
  }
}
