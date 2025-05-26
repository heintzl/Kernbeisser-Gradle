package kernbeisser.Windows.CashierShoppingMask;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import kernbeisser.DBEntities.Repositories.TransactionRepository;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.InvalidReportNoException;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.AccountingReport;
import kernbeisser.Reports.UserBalanceReport;
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
      AtomicBoolean success = new AtomicBoolean(true);
      List<Transaction> unreportedTransactions = TransactionRepository.getUnreportedTransactions();
      if (unreportedTransactions.isEmpty()) {
        return 0;
      }
      long no = TransactionRepository.getLastReportNo() + 1;
      new AccountingReport(no, true)
          .exportPdfToCloud(
              "Erstelle Buchhaltungsbericht",
              UnexpectedExceptionHandler::showUnexpectedErrorWarning);

      Instant lastUserBalance = Instant.parse(Setting.LAST_USER_BALANCE_REPORT.getStringValue());
      if (lastUserBalance
          .plus(Setting.USER_BALANCE_REPORT_INTERVAL.getIntValue(), ChronoUnit.DAYS)
          .isBefore(Instant.now())) {
        new UserBalanceReport(no, true)
            .exportPdfToCloud(
                "Kontostände werden exportiert",
                (e) -> {
                  success.set(false);
                });
        if (success.get()) {
          Setting.LAST_USER_BALANCE_REPORT.changeValue(
              Instant.now().truncatedTo(ChronoUnit.DAYS).toString());
        }
      }
      return 0;
    } catch (NoTransactionsFoundException e) {
      return 0;
    } catch (InvalidReportNoException e) {
      return TransactionRepository.getUnreportedTransactions().size();
    }
  }
}
