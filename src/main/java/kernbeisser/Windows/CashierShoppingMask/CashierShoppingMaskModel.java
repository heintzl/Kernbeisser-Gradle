package kernbeisser.Windows.CashierShoppingMask;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Reports.UserBalanceReport;
import kernbeisser.Reports.UserNameObfuscation;
import kernbeisser.Windows.AccountingReports.AccountingReportsModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class CashierShoppingMaskModel implements IModel<CashierShoppingMaskController> {
  private boolean openLock = false;

  public Collection<User> searchUser(String searchQuery) {
    return User.defaultSearch(searchQuery, 500);
  }

  public static void printAccountingReports(
      List<Transaction> reportTransactions, Consumer<Boolean> resultConsumer) {
    long no = Transaction.getLastReportNo() + 1;
    if (AccountingReportsModel.exportAccountingReports(
        reportTransactions, no, UserNameObfuscation.WITHOUTPAYIN, true)) {
      Transaction.writeAccountingReportNo(reportTransactions, no);
    } else {
      resultConsumer.accept(false);
    }

    Instant lastUserBalance = Instant.parse(Setting.LAST_USER_BALANCE_REPORT.getStringValue());
    if (lastUserBalance
        .plus(Setting.USER_BALANCE_REPORT_INTERVAL.getIntValue(), ChronoUnit.DAYS)
        .isBefore(Instant.now())) {
      AtomicBoolean success = new AtomicBoolean(true);
      new UserBalanceReport(no, false)
          .sendToPrinter(
              "Kontostände werden gedruckt",
              (e) -> {
                success.set(false);
              });
      if (success.get()) {
        Setting.LAST_USER_BALANCE_REPORT.changeValue(
            Instant.now().truncatedTo(ChronoUnit.DAYS).toString());
      }
    }
  };
}
