package kernbeisser.Reports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.*;
import kernbeisser.Reports.ReportDTO.DeliveryBillItem;
import kernbeisser.Useful.Constants;

public class DeliveryBillReport extends Report {

  public DeliveryBillReport() {
    super(ReportFileNames.DELIVERY_BILL_REPORT_FILENAME);
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
    Collection<DeliveryBillItem> details =
        QueryBuilder.selectAll(PreOrder.class)
            .where(
                PreOrder_.delivery.isNull(),
                PreOrder_.orderedOn.isNull().not(),
                PreOrder_.user.child(User_.id).eq(Constants.SHOP_USER_ID).not())
            .orderBy(PreOrder_.user.child(User_.username).asc())
            .getResultList()
            .stream()
            // .filter(p -> p.getCatalogEntry() != null)
            .map(DeliveryBillItem::ofPreOrder)
            .toList();
    return details;
  }
}
