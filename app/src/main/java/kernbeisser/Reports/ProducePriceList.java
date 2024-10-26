package kernbeisser.Reports;

import java.time.Instant;
import java.util.*;
import kernbeisser.DBEntities.Repositories.ArticleRepository;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.ReportDTO.PriceListReportArticle;
import kernbeisser.Windows.Supply.SupplySelector.LineContent;
import kernbeisser.Windows.Supply.SupplySelector.ResolveStatus;

public class ProducePriceList extends Report {
  private final Collection<PriceListReportArticle> priceListReportContents;
  private final String priceListName;

  public ProducePriceList(Collection<LineContent> contents, String priceListName) {
    super(ReportFileNames.PRODUCE_PRICELIST);
    // setDuplexPrint(false);
    Map<Integer, Instant> lastDeliveries = ArticleRepository.getLastDeliveries();
    this.priceListReportContents = new ArrayList<>();
    for (LineContent c : contents) {
      if (c.getStatus() == ResolveStatus.PRODUCE) {
        try {
          priceListReportContents.add(PriceListReportArticle.ofProduceLineContent(c));
        } catch (InputMismatchException e) {
          UnexpectedExceptionHandler.showUnexpectedErrorWarning(e);
        }
      }
    }
    this.priceListName = priceListName;
  }

  @Override
  String createOutFileName() {
    return "Preisliste " + priceListName;
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> params = new HashMap<>();
    params.put("priceList", priceListName);
    return params;
  }

  @Override
  Collection<?> getDetailCollection() {
    return priceListReportContents;
  }
}
