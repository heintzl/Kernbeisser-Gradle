package kernbeisser.Windows.CashierShoppingMask;

import java.util.List;
import kernbeisser.Config.Config;
import kernbeisser.DBEntities.Repositories.TransactionRepository;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.Exeptions.InvalidReportNoException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.AccountingReport;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class CashierShoppingMaskModel implements IModel<CashierShoppingMaskController> {
  private boolean openLock = false;

  private static void markTransactionsAsReported(
      List<Transaction> transactions, String fileName, long no) {
    if (!transactions.isEmpty()) {
      if (Config.getConfig()
          .getReports()
          .getCloudOutputDirectory()
          .toPath()
          .resolve(fileName + ".pdf")
          .toFile()
          .exists()) {
        TransactionRepository.writeAccountingReportNo(transactions, no);
      }
    }
  }

  public static int printAccountingReports() {
    try {
      List<Transaction> unreportedTransactions = TransactionRepository.getUnreportedTransactions();
      if (unreportedTransactions.isEmpty()) {
        return 0;
      }
      long no = TransactionRepository.getLastReportNo() + 1;
      AccountingReport accountingReport = new AccountingReport(no, true);
      accountingReport.exportPdfToCloudAndThen(
          "Erstelle Buchhaltungsbericht",
          UnexpectedExceptionHandler::showUnexpectedErrorWarning,
          () ->
              markTransactionsAsReported(
                  unreportedTransactions, accountingReport.getSafeOutFileName(), no));
      return 0;
    } catch (NoTransactionsFoundException e) {
      return 0;
    } catch (InvalidReportNoException e) {
      return TransactionRepository.getUnreportedTransactions().size();
    }
  }
}
