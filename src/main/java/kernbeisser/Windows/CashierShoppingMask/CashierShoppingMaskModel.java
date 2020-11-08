package kernbeisser.Windows.CashierShoppingMask;

import java.util.Collection;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.SaleSessionType;
import kernbeisser.Enums.Setting;
import kernbeisser.Reports.AccountingReport;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Data;

@Data
public class CashierShoppingMaskModel implements IModel<CashierShoppingMaskController> {
  public Collection<User> searchUser(String searchQuery) {
    return User.defaultSearch(searchQuery, 500);
  }

  boolean isShoppingMaskOpened() {
    long from = Setting.LAST_PRINTED_BON_NR.getLongValue();
    long id = getLastAssistedPurchaseId();
    return (from < id);
  }

  void printTillRoll(Consumer<Boolean> resultConsumer) {
    long from = Setting.LAST_PRINTED_BON_NR.getLongValue();
    long id = getLastAssistedPurchaseId();
    if (from == id) {
      resultConsumer.accept(false);
      return;
    }
    Setting.LAST_PRINTED_BON_NR.changeValue(id);
    AccountingReport accountingReportManager = new AccountingReport(from + 1, id);
    accountingReportManager.exportPdf(
        "Ladendienst wird gedruckt",
        (e) -> {
          Setting.LAST_PRINTED_BON_NR.changeValue(from);
          resultConsumer.accept(false);
          Tools.showUnexpectedErrorWarning(e);
        });
    resultConsumer.accept(true);
  };

  long getLastAssistedPurchaseId() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    return em.createQuery(
            "select max(p.id) from Purchase p where p.session.seller.id = :lid and p.session.sessionType = :t",
            Long.class)
        .setParameter("lid", LogInModel.getLoggedIn().getId())
        .setParameter("t", SaleSessionType.ASSISTED)
        .getSingleResult();
  }
}
