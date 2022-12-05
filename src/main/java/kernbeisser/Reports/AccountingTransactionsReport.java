package kernbeisser.Reports;

import java.util.*;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Exeptions.NoTransactionsFoundException;

public class AccountingTransactionsReport extends Report {

  private final long reportNo;
  private final List<Transaction> transactions;
  private final UserNameObfuscation withNames;
  private final boolean printValueSums;

  public AccountingTransactionsReport(
      long reportNo,
      List<Transaction> transactions,
      UserNameObfuscation withNames,
      boolean printValueSums)
      throws NoTransactionsFoundException {
    super(
        "accountingTransactionReportFileName",
        String.format("KernbeisserBuchhaltungEinSonderzahlungen_%d", reportNo));
    this.reportNo = reportNo;
    this.transactions = transactions;
    this.withNames = withNames;
    this.printValueSums = printValueSums;
  }

  @Override
  Map<String, Object> getReportParams() {
    long lastTransactionId = transactions.stream().mapToLong(Transaction::getId).max().getAsLong();
    Map<String, Object> reportParams =
        UserGroup.getValueAggregatesAtTransactionId(lastTransactionId);
    reportParams.put("userGroup", User.getKernbeisserUser().getUserGroup());
    reportParams.put("reportNo", reportNo);
    reportParams.put("reportTitle", AccountingReport.getReportTitle(reportNo, transactions));
    reportParams.put("printValueSums", printValueSums);
    return reportParams;
  }

  @Override
  Collection<?> getDetailCollection() {
    return transactions.stream()
        .map(t -> t.withUserIdentifications(withNames))
        .collect(Collectors.toList());
  }
}
