package kernbeisser.Windows.CashierShoppingMask;

import java.util.Collection;
import java.util.List;
import kernbeisser.DBEntities.Repositories.TransactionRepository;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Exeptions.InvalidReportNoException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.AccountingReport;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class CashierShoppingMaskModel implements IModel<CashierShoppingMaskController> {
  private boolean openLock = false;

  public Collection<User> searchUser(String searchQuery) {
    return User.defaultSearch(searchQuery, 500);
  }

  public static int printAccountingReports() {
    try {
      List<Transaction> unreportedTransactions = TransactionRepository.getUnreportedTransactions();
      if (unreportedTransactions.isEmpty()) {
        return 0;
      }
      long no = TransactionRepository.getLastReportNo() + 1;
      new AccountingReport(no, true)
          .exportPdfToCloud(
              "Erstelle Buchhaltungsbericht",
              UnexpectedExceptionHandler::showUnexpectedErrorWarning);
      return 0;
    } catch (NoTransactionsFoundException e) {
      return 0;
    } catch (InvalidReportNoException e) {
      return TransactionRepository.getUnreportedTransactions().size();
    }
  }
}
