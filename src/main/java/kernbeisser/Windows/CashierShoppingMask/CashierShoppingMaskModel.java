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
import kernbeisser.Windows.AccountingReports.AccountingReportsModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class CashierShoppingMaskModel implements IModel<CashierShoppingMaskController> {
  private boolean openLock = false;

  public Collection<User> searchUser(String searchQuery) {
    return User.defaultSearch(searchQuery, 500);
  }

  void printAccountingReports(Consumer<Boolean> resultConsumer) {
    long from = Setting.LAST_PRINTED_TRANSACTION_ID.getLongValue();
    long id = Transaction.getLastTransactionId();
    long no = Setting.LAST_PRINTED_ACCOUNTING_REPORT_NR.getLongValue() + 1;
    List<Transaction> reportTransactions = Transaction.getTransactionRange(from + 1, id);
    if (AccountingReportsModel.exportAccountingReports(reportTransactions, no, false)) {
      Setting.LAST_PRINTED_TRANSACTION_ID.changeValue(id);
      Setting.LAST_PRINTED_ACCOUNTING_REPORT_NR.changeValue(no);
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
