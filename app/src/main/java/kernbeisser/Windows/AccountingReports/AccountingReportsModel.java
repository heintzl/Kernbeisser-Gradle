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

public class AccountingReportsModel implements IModel<AccountingReportsController> {

  @Getter private final ExportTypes[] exportTypes = ExportTypes.values();
  @Getter private final List<String> userKeySortOrders = Arrays.asList("Id", "Name");
  @Getter private final UserNameObfuscation[] userNameObfuscations = UserNameObfuscation.values();

  public AccountingReportsModel() {}

  public static void exportReport(
      ExportTypes exportType,
      Report report,
      String message,
      boolean duplexPrint,
      Consumer<Throwable> consumeJRException) {
    switch (exportType) {
      case PRINT:
        report.setDuplexPrint(duplexPrint);
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
      List<Transaction> transactions, long no, UserNameObfuscation withNames, boolean duplexPrint) {
    AtomicBoolean success = new AtomicBoolean(true);
    boolean printValueSums = true;
    Report report;
    if (transactions.stream().anyMatch(Transaction::isPurchase)) {
      report = new AccountingReport(no, transactions, withNames == UserNameObfuscation.NONE);
      report.setDuplexPrint(duplexPrint);
      report.sendToPrinter(
          "Ladendienst wird gedruckt",
          (e) -> {
            success.set(false);
          });
      printValueSums = false;
    }
    List<Transaction> otherTransactions =
        transactions.stream()
            .filter(Transaction::isAccountingReportTransaction)
            .collect(Collectors.toList());
    if (!otherTransactions.isEmpty()) {
      report = new AccountingTransactionsReport(no, otherTransactions, withNames, printValueSums);
      report.setDuplexPrint(duplexPrint);
      report.sendToPrinter(
          "Ladendienst wird gedruckt",
          (e) -> {
            success.set(false);
          });
    }
    return success.get();
  }
}
