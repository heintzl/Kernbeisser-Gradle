package kernbeisser.Windows.Tillroll;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Reports.ReportManager;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import net.sf.jasperreports.engine.JRException;

public class TillrollModel implements IModel<TillrollController> {

  @Getter private final ExportTypes[] exportTypes = ExportTypes.values();

  public TillrollModel() {}

  private List<ShoppingItem> getTillrollitems(Instant start, Instant end) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    List<ShoppingItem> items =
        em.createQuery(
                "select si from ShoppingItem si where si.purchase.createDate between :stdate and :endate",
                ShoppingItem.class)
            .setParameter("stdate", start)
            .setParameter("endate", end)
            .getResultList();
    em.close();
    return items;
  }

  public void exportTillroll(ExportTypes exportType, int days)
      throws UnsupportedOperationException, IncorrectInput {
    Instant end = Instant.now();
    Instant start = end.minus(days, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
    List<ShoppingItem> items = getTillrollitems(start, end);
    if (items.size() == 0) {
      throw new IncorrectInput("Leere Bonrolle");
    }
    if (exportType == ExportTypes.PDF || exportType == ExportTypes.PRINT) {
      try {
        ReportManager tillroll = new ReportManager();
        tillroll.initTillrollPrint(items, start, end);
        if (exportType == ExportTypes.PDF) {
          tillroll.exportPdf();
        } else {
          tillroll.sendToPrinter();
        }
      } catch (JRException e) {
        Tools.showUnexpectedErrorWarning(e);
      }

    } else {
      throw new UnsupportedOperationException();
    }
  }
}
