package kernbeisser.Reports;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import kernbeisser.DBConnection.PredicateFactory;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.*;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Reports.ReportDTO.ArticleLossGroup;

public class LossAnalysisReport extends Report {
  private final Instant start;
  private final Instant endExclusive;

  public LossAnalysisReport(Instant start, Instant endExclusive) {
    super(ReportFileNames.LOSSANALYSIS_REPORT_FILENAME);
    this.start = start;
    this.endExclusive = endExclusive;
  }

  @Override
  String createOutFileName() {
    return String.format(
        "KernbeisserSchwundanalyse_%s_%s", Timestamp.from(start), Timestamp.from(endExclusive));
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
  public Collection<ArticleLossGroup> getDetailCollection() {
    Map<Integer, ArticleLossGroup> lossGroups = new HashMap<>();
    lossGroups.put(1, new ArticleLossGroup(1, "100% Reduktion"));
    lossGroups.put(0, new ArticleLossGroup(0, "andere Waren"));
    // iterate the first 4 ArticleConstants
    for (int i = -1; i > -5; i--) {
      Article abstractArticle =
          ArticleRepository.getByKbNumber(i, false)
              .orElseThrow(NoSuchElementException::new)
              .getValue();
      lossGroups.put(i, new ArticleLossGroup(i, abstractArticle.getName()));
    }
    for (ShoppingItem item :
        QueryBuilder.selectAll(ShoppingItem.class)
            .where(
                PredicateFactory.between(
                    ShoppingItem_.purchase.child(Purchase_.createDate), start, endExclusive))
            .getResultList()) {
      int groupNumber = item.getKbNumber();
      if (groupNumber > -1 || groupNumber < -4) {
        groupNumber = 0;
      }
      ArticleLossGroup lossGroup = lossGroups.get(groupNumber);
      lossGroup.setNetPurchaseSum(lossGroup.getNetPurchaseSum() + item.getNetPrice());
      lossGroup.setGrossRetailSum(lossGroup.getGrossRetailSum() + item.getRetailPrice());
      if (item.getDiscount() == 100) {
        lossGroup = lossGroups.get(1);
        lossGroup.setNetPurchaseSum(lossGroup.getNetPurchaseSum() + item.getNetPrice());
        lossGroup.setGrossRetailSum(lossGroup.getGrossRetailSum() + item.getUnreducedRetailPrice());
        lossGroup.setCount(lossGroup.getCount() + 1);
      }
    }
    return lossGroups.values();
  }
}
