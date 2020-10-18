package kernbeisser.Windows.Tillroll;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Reports.ReportManager;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.time.DateUtils;

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
      throws UnsupportedOperationException, IncorrectInput, JRException {
    Instant end = Instant.now();
    Instant start =
        DateUtils.truncate(Date.from(end.minus(days, ChronoUnit.DAYS)), Calendar.DATE).toInstant();
    List<ShoppingItem> items = getTillrollitems(start, end);
    if (items.size() == 0) {
      throw new IncorrectInput("Leere Bonrolle");
    }
    switch (exportType) {
      case PRINT:
      case PDF:
        ReportManager tillroll = new ReportManager();
        tillroll.initTillrollPrint(items, start, end);
        if (exportType == ExportTypes.PDF) {
          tillroll.exportPdf();
        } else {
          tillroll.sendToPrinter();
        }
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }
}
