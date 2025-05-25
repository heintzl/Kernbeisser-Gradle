package kernbeisser.Windows.AccountingReports;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.swing.*;

import kernbeisser.DBEntities.Repositories.TransactionRepository;
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
      case PRINT -> {
        report.setDuplexPrint(duplexPrint);
        report.sendToPrinter(message, consumeJRException);
      }
      case PDF -> report.exportPdf(message, consumeJRException);
      case CLOUD -> report.exportPdfToCloud(message, consumeJRException);
      default -> throw new UnsupportedOperationException();
    }
  }

  public static boolean exportAccountingReports(
      List<Transaction> transactions,
      long no,
      UserNameObfuscation withNames,
      boolean duplexPrint,
      ExportTypes exportType) {
    AtomicBoolean success = new AtomicBoolean(true);
    boolean printValueSums = true;
    Report report;
    if (transactions.stream().anyMatch(TransactionRepository::isPurchase)) {
      report = new AccountingReport(no, transactions, withNames == UserNameObfuscation.NONE);
      report.setDuplexPrint(duplexPrint);
      report.sendToPrinter(
          "Erstelle Ladendienst-Bericht",
          (e) -> {
            success.set(false);
          });
      printValueSums = false;
    }
    return success.get();
  }
}
