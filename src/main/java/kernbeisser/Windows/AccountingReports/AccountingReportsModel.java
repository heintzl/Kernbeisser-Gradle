package kernbeisser.Windows.AccountingReports;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Reports.*;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;
import lombok.var;

public class AccountingReportsModel implements IModel<AccountingReportsController> {

  @Getter private final ExportTypes[] exportTypes = ExportTypes.values();
  @Getter private final List<String> userKeySortOrders = Arrays.asList("Id", "Name");

  public AccountingReportsModel() {}

  public static void exportReport(
      ExportTypes exportType,
      Report report,
      String message,
      Consumer<Throwable> consumeJRException) {
    switch (exportType) {
      case PRINT:
        report.sendToPrinter(message, consumeJRException);
        break;
      case PDF:
        report.exportPdf(message, consumeJRException);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public static boolean exportAccountingReports(
      List<Transaction> transactions, long no, boolean withNames) {
    AtomicBoolean success = new AtomicBoolean(true);
    boolean printValueSums = true;
    if (transactions.stream().anyMatch(Transaction::isPurchase)) {
      new AccountingReport(no, transactions, withNames)
          .sendToPrinter(
              "Ladendienst wird gedruckt",
              (e) -> {
                success.set(false);
              });
      printValueSums = false;
    }
    var otherTransactions =
        transactions.stream()
            .filter(Transaction::isAccountingReportTransaction)
            .collect(Collectors.toList());
    if (!otherTransactions.isEmpty()) {
      new AccountingTransactionsReport(no, otherTransactions, withNames, printValueSums)
          .sendToPrinter(
              "Ladendienst wird gedruckt",
              (e) -> {
                success.set(false);
              });
    }
    return success.get();
  }
}
