package kernbeisser.Windows.CashierShoppingMask;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Reports.AccountingReport;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;

@Data
public class CashierShoppingMaskModel implements IModel<CashierShoppingMaskController> {
  private boolean openLock = false;

  public Collection<User> searchUser(String searchQuery) {
    return User.defaultSearch(searchQuery, 500);
  }

  void printTillRoll(Consumer<Boolean> resultConsumer) {
    long from = Setting.LAST_PRINTED_TRANSACTION_ID.getLongValue();
    long id = Transaction.getLastTransactionId();
    long no = Setting.LAST_PRINTED_ACCOUNTING_REPORT_NR.getLongValue() + 1;
    List<Transaction> reportTransactions = Transaction.getTransactionRange(from + 1, id);
    AccountingReport accountingReportManager = new AccountingReport(no, reportTransactions, false);
    accountingReportManager.sendToPrinter(
        "Ladendienst wird gedruckt",
        (e) -> {
          resultConsumer.accept(false);
        });
    resultConsumer.accept(true);
    Setting.LAST_PRINTED_TRANSACTION_ID.changeValue(id);
    Setting.LAST_PRINTED_ACCOUNTING_REPORT_NR.changeValue(no);
  };
}
