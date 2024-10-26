package kernbeisser.Reports;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.ShoppingItemSum;
import kernbeisser.Enums.VAT;

public class InvoiceReport extends Report {
  private final Purchase purchase;
  private Instant at;

  public InvoiceReport(Purchase purchase) {
    super(ReportFileNames.INVOICE_REPORT_FILENAME);
    this.purchase = purchase;
  }

  @Override
  String createOutFileName() {
    return String.format(
        "%d_%s_%s_%s",
        purchase.getBonNo(),
        purchase.getSession().getCustomer().getFirstName(),
        purchase.getSession().getCustomer().getSurname(),
        purchase.getCreateDate().toString());
  }

  public InvoiceReport atPurchaseTime() {
    at = purchase.getCreateDate();
    return this;
  }

  @Override
  Collection<?> getDetailCollection() {
    Collection<ShoppingItem> items = purchase.getAllItems();
    for (ShoppingItem item : items) {
      if (item.getItemMultiplier() > 1) {
        double retailPrice = item.getItemRetailPrice();
        if (item.isWeighAble() && item.isContainerDiscount()) {
          retailPrice /= item.getAmount() * item.getMetricUnits().getBaseFactor();
        }
        String suffix =
            item.isWeighAble() ? "/" + item.getMetricUnits().getDisplayUnit().getShortName() : "";
        item.setName("%s à %.2f€%s".formatted(item.getName(), retailPrice, suffix));
      }
    }
    return items;
  }

  @Override
  Map<String, Object> getReportParams() {
    Collection<ShoppingItem> items = purchase.getAllItems();
    double credit;
    if (at == null) {
      credit = purchase.getSession().getCustomer().getUserGroup().getValue();
    } else {
      credit = purchase.getSession().getCustomer().valueAt(at) - purchase.getSum();
    }
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("BonNo", purchase.getBonNo());
    reportParams.put("Customer", purchase.getSession().getCustomer().getFullName());
    reportParams.put("Seller", purchase.getSession().getSeller().getFullName());
    reportParams.put("Credit", credit);
    reportParams.put("PurchaseDate", purchase.getCreateDate());
    reportParams.put("CreditWarning", credit <= Setting.CREDIT_WARNING_THRESHOLD.getDoubleValue());
    reportParams.put("VatValueLow", purchase.guessVatValue(VAT.LOW));
    reportParams.put("VatValueHigh", purchase.guessVatValue(VAT.HIGH));
    reportParams.put("SumTotal", purchase.getSum());
    reportParams.put("VatSumLow", ShoppingItem.getSum(ShoppingItemSum.VAT_VATLOW, items));
    reportParams.put("VatSumHigh", ShoppingItem.getSum(ShoppingItemSum.VAT_VATHIGH, items));

    return reportParams;
  }
}
