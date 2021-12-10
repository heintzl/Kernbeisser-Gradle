package kernbeisser.Windows.CashierShoppingMask;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Reports.AccountingReport;
import kernbeisser.Reports.AccountingTransactionsReport;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;
import lombok.var;

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
    new AccountingReport(no, reportTransactions, true)
        .sendToPrinter(
            "Ladendienst wird gedruckt",
            (e) -> {
              resultConsumer.accept(false);
            });
    resultConsumer.accept(true);
    var reportOtherTransactions =
        reportTransactions.stream()
            .filter(
                t ->
                    t.getTransactionType() == TransactionType.PAYIN
                        || t.getTransactionType() == TransactionType.INITIALIZE
                        || (t.getTransactionType() == TransactionType.USER_GENERATED
                            && t.relationToKernbeisser() != 0))
            .collect(Collectors.toList());
    if (!reportOtherTransactions.isEmpty()) {
      new AccountingTransactionsReport(no, reportOtherTransactions, true)
          .sendToPrinter(
              "Ladendienst wird gedruckt",
              (e) -> {
                resultConsumer.accept(false);
              });
    }
    Setting.LAST_PRINTED_TRANSACTION_ID.changeValue(id);
    Setting.LAST_PRINTED_ACCOUNTING_REPORT_NR.changeValue(no);
  };
}
