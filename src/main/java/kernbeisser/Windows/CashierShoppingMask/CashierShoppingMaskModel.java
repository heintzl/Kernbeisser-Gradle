package kernbeisser.Windows.CashierShoppingMask;

import java.util.Collection;
import java.util.function.Consumer;
import kernbeisser.DBEntities.Purchase;
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
    long from = Setting.LAST_PRINTED_BON_NR.getLongValue();
    long id = Purchase.getLastBonNo();
    long no = Setting.LAST_PRINTED_ACOUNTING_REPORT_NR.getLongValue() + 1;
    AccountingReport accountingReportManager = new AccountingReport(no, from + 1, id, false);
    accountingReportManager.sendToPrinter(
        "Ladendienst wird gedruckt",
        (e) -> {
          resultConsumer.accept(false);
        });
    resultConsumer.accept(true);
    Setting.LAST_PRINTED_BON_NR.changeValue(id);
    Setting.LAST_PRINTED_ACOUNTING_REPORT_NR.changeValue(no);
  };
}
