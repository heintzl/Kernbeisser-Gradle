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
  public Collection<User> searchUser(String searchQuery) {
    return User.defaultSearch(searchQuery, 500);
  }

  void printTillRoll(Consumer<Boolean> resultConsumer) {
    long from = Setting.LAST_PRINTED_BON_NR.getLongValue();
    long id = Purchase.getLastBonNo();
    AccountingReport accountingReportManager = new AccountingReport(from + 1, id, false);
    accountingReportManager.sendToPrinter(
        "Ladendienst wird gedruckt",
        (e) -> {
          resultConsumer.accept(false);
        });
    resultConsumer.accept(true);
    Setting.LAST_PRINTED_BON_NR.setValue(id);
  };
}
