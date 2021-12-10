package kernbeisser.Reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.NoPurchasesFoundException;

public class AccountingTransactionsReport extends Report {

  private final long reportNo;
  private final List<Transaction> transactions;
  private final boolean withNames;

  public AccountingTransactionsReport(
      long reportNo, List<Transaction> transactions, boolean withNames)
      throws NoPurchasesFoundException {
    super(
        "accountingTransactionReportFileName",
        String.format("KernbeisserBuchhaltungEinSonderzahlungen_%d", reportNo));
    this.reportNo = reportNo;
    this.transactions = transactions;
    this.withNames = withNames;
  }

  @Override
  Map<String, Object> getReportParams() {
    Map<String, Object> reportParams = new HashMap<>();
    reportParams.put("userGroup", User.getKernbeisserUser().getUserGroup());
    reportParams.put("reportNo", reportNo);
    reportParams.put(
        "reportTitle", reportNo == 0 ? "Umsatzbericht" : "LD-Endabrechnung Nr. " + reportNo);
    return reportParams;
  }

  @Override
  Collection<?> getDetailCollection() {
    return transactions.stream()
        .map(t -> t.withUserIdentifications(withNames))
        .collect(Collectors.toList());
  }
}
