package kernbeisser.Reports;

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

  public InvoiceReport(Purchase purchase) {
    super(
        "invoiceFileName",
        String.format(
            "%d_%s_%s_%s",
            purchase.getId(),
            purchase.getSession().getCustomer().getFirstName(),
            purchase.getSession().getCustomer().getSurname(),
            purchase.getCreateDate().toString()));
    this.purchase = purchase;
  }

  @Override
  Collection<?> getDetailCollection() {
    return purchase.getAllItems();
  }

  @Override
  Map<String, Object> getReportParams() {
    Collection<ShoppingItem> items = purchase.getAllItems();
    double credit = purchase.getSession().getCustomer().getUserGroup().getValue();
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("BonNo", purchase.getId());
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
