package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kernbeisser.DBEntities.*;
import kernbeisser.Reports.ReportDTO.DeliveryBillItem;

public class DeliveryBillReport extends Report {

  private final List<PreOrder> preOrders;

  public DeliveryBillReport(List<PreOrder> preOrders) {
    super(ReportFileNames.DELIVERY_BILL_REPORT_FILENAME);
    this.preOrders = preOrders;

    setDuplexPrint(false);
  }

  @Override
  String createOutFileName() {
    return "VB-Lieferscheine_" + LocalDate.now();
  }

  @Override
  Map<String, Object> getReportParams() {
    return new HashMap<>();
  }

  @Override
  Collection<?> getDetailCollection() {
    return preOrders.stream().map(DeliveryBillItem::ofPreOrder).toList();
  }
}
