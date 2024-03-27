package kernbeisser.Reports;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Reports.ReportDTO.PriceListReportArticle;
import kernbeisser.Useful.Date;

public class ArticleLabel extends Report {
  private final List<Article> articles;

  public ArticleLabel(List<Article> articles) {
    super(ReportFileNames.ARTICLE_LABEL_REPORT_FILENAME);
    setDuplexPrint(false);
    this.articles = articles;
  }

  @Override
  String createOutFileName() {
    return "Etiketten_" + Date.INSTANT_DATE_TIME.format(Instant.now());
  }

  @Override
  Map<String, Object> getReportParams() {
    return null;
  }

  @Override
  Collection<?> getDetailCollection() {
    Map<Integer, Instant> lastDeliveries = ArticleRepository.getLastDeliveries();
    return articles.stream()
        .map(a -> PriceListReportArticle.ofArticle(a, lastDeliveries))
        .sorted(
            Comparator.comparing(PriceListReportArticle::getSuppliersShortName)
                .thenComparingInt(PriceListReportArticle::getSuppliersItemNumber))
        .collect(Collectors.toList());
  }
}
