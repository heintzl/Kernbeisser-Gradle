package kernbeisser.Windows.CashierShoppingMask;

import java.util.Collection;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.swing.ProgressMonitor;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Setting;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Reports.ReportManager;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;
import lombok.Data;
import net.sf.jasperreports.engine.JRException;

@Data
public class CashierShoppingMaskModel implements IModel<CashierShoppingMaskController> {
  public Collection<User> searchUser(String searchQuery) {
    return User.defaultSearch(searchQuery, 500);
  }

  private boolean shoppingMaskOpened = false;

  void printTillRoll(Consumer<Boolean> resultConsumer) {
    new Thread(
            () -> {
              ProgressMonitor pm =
                  new ProgressMonitor(
                      null, "Ladendienst wird gedruckt", "Initialiesiere Druckerservice...", 0, 2);

              EntityManager em = DBConnection.getEntityManager();
              long id =
                  em.createQuery(
                          "select max(p.id) from Purchase p where p.session.seller.id = :lid",
                          Long.class)
                      .setParameter("lid", LogInModel.getLoggedIn().getId())
                      .getSingleResult();
              long from = Setting.LAST_PRINTED_BON_NR.getLongValue();
              if (from == id) {
                resultConsumer.accept(false);
                return;
              }
              Setting.LAST_PRINTED_BON_NR.changeValue(id);
              try {
                ReportManager.initAccountingReportPrint(from + 1, id);
                pm.setProgress(1);
                pm.setNote("Exportiere PDF..");
                new ReportManager().exportPdf();
              } catch (JRException | InvalidVATValueException e) {
                Setting.LAST_PRINTED_BON_NR.changeValue(from);
                resultConsumer.accept(false);
                Tools.showUnexpectedErrorWarning(e);
              }

              pm.setNote("Fertig");
              pm.close();
              resultConsumer.accept(true);
            })
        .start();
  }
}
