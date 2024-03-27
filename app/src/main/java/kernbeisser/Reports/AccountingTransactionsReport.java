package kernbeisser.Reports;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    super(ReportFileNames.ACCOUNTING_TRANSACTION_REPORT_FILENAME);
    this.reportNo = reportNo;
    this.transactions = transactions;
    this.withNames = withNames;
    this.printValueSums = printValueSums;
  }

  @Override
  String createOutFileName() {
    return String.format("KernbeisserBuchhaltungEinSonderzahlungen_%d", reportNo);
  }

  @Override
  Map<String, Object> getReportParams() {
    Instant lastTransactionInstant =
        transactions.stream().map(Transaction::getDate).max(Instant::compareTo).get();
    Map<String, Object> reportParams = UserGroup.getValueAggregatesAt(lastTransactionInstant);
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
