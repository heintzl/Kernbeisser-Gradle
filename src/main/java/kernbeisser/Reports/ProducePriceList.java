package kernbeisser.Reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBEntities.Articles;
import kernbeisser.Reports.ReportDTO.PriceListReportArticle;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Supply.SupplySelector.LineContent;
import kernbeisser.Windows.Supply.SupplySelector.ResolveStatus;
import lombok.var;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class ProducePriceList extends Report {
  private final Collection<PriceListReportArticle> priceListReportContents;
  private final String priceListName;

  public ProducePriceList(Collection<LineContent> contents, String priceListName) {
    super(ReportFileNames.PRODUCE_PRICELIST);
    // setDuplexPrint(false);
    var lastDeliveries = Articles.getLastDeliveries();
    this.priceListReportContents = new ArrayList<>();
    for (LineContent c : contents) {
      if (c.getStatus() == ResolveStatus.PRODUCE) {
        try {
          priceListReportContents.add(PriceListReportArticle.ofProduceLineContent(c));
        } catch (InvalidInputException e) {
          Tools.showUnexpectedErrorWarning(e);
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
