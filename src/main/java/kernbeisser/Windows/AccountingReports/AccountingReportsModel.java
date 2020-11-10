package kernbeisser.Windows.AccountingReports;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Reports.*;
import kernbeisser.Windows.MVC.IModel;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;

public class AccountingReportsModel implements IModel<AccountingReportsController> {

  @Getter private final ExportTypes[] exportTypes = ExportTypes.values();
  @Getter private final List<String> userKeySortOrders = Arrays.asList("Id", "Name");

  public AccountingReportsModel() {}

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

  private void exportReport(
      ExportTypes exportType,
      Report report,
      String message,
      Consumer<Throwable> consumeJRException) {
    switch (exportType) {
      case PRINT:
      case PDF:
        if (exportType == ExportTypes.PDF) {
          report.exportPdf(message, consumeJRException);
        } else {
          report.sendToPrinter(message, consumeJRException);
        }
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public void exportTillroll(
      ExportTypes exportType, int days, Consumer<Throwable> consumePdfException) {
    Instant end = Instant.now();
    Instant start =
        DateUtils.truncate(Date.from(end.minus(days, ChronoUnit.DAYS)), Calendar.DATE).toInstant();
    List<ShoppingItem> items = getTillrollitems(start, end);
    if (items.size() == 0) {
      consumePdfException.accept(new IncorrectInput("Leere Bonrolle"));
      return;
    }
    TillrollReport reportManager = new TillrollReport(items, start, end);
    exportReport(exportType, reportManager, "Bonrolle wird erstellt", consumePdfException);
  }

  public void exportAccountingReport(
      ExportTypes exportType,
      int startBon,
      int endBon,
      boolean withNames,
      Consumer<Throwable> consumePdfException) {
    AccountingReport report = new AccountingReport(startBon, endBon, withNames);
    exportReport(
        exportType, report, "Ladendienst Endabrechnung wird erstellt", consumePdfException);
  }

  public void exportUserBalance(
      ExportTypes exportType, boolean withNames, Consumer<Throwable> consumePdfException) {
    UserBalanceReport report = new UserBalanceReport(withNames);
    exportReport(exportType, report, "Guthabenstände werden erstellt", consumePdfException);
  }

  public void exportKeyUserList(
      ExportTypes exportType, String sortOrder, Consumer<Throwable> consumePdfException) {
    KeyUserList report = new KeyUserList(sortOrder);
    exportReport(exportType, report, "Benutzer-Schlüssel-Liste wird erstellt", consumePdfException);
  }
}
