package kernbeisser.Windows.AccountingReports;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.StatementType;
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

  private List<ShoppingItem> getTillrollitems(Instant start, Instant endExclusive) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return em.createQuery(
            "select si from ShoppingItem si where si.purchase.createDate >= :start and purchase.createDate < :end",
            ShoppingItem.class)
        .setParameter("start", start)
        .setParameter("end", endExclusive)
        .getResultList();
  }

  private static void exportReport(
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
      ExportTypes exportType,
      Calendar startDate,
      Calendar endDate,
      Consumer<Throwable> consumePdfException) {
    Instant start = DateUtils.truncate(startDate, Calendar.DATE).toInstant();
    Instant endExclusive =
        DateUtils.truncate(endDate, Calendar.DATE).toInstant().plus(1, ChronoUnit.DAYS);
    // Instant start =
    //    DateUtils.truncate(Date.from(end.minus(days, ChronoUnit.DAYS)),
    // Calendar.DATE).toInstant();
    List<ShoppingItem> items = getTillrollitems(start, endExclusive);
    if (items.size() == 0) {
      consumePdfException.accept(new IncorrectInput("Leere Bonrolle"));
      return;
    }
    exportReport(
        exportType,
        new TillrollReport(items, start, endExclusive),
        "Bonrolle wird erstellt",
        consumePdfException);
  }

  public static void exportAccountingReport(
      ExportTypes exportType,
      long startBon,
      long endBon,
      boolean withNames,
      Consumer<Throwable> consumePdfException) {
    exportReport(
        exportType,
        new AccountingReport(0, startBon, endBon, withNames),
        "Ladendienst Endabrechnung wird erstellt",
        consumePdfException);
  }

  public void exportUserBalance(
      ExportTypes exportType, boolean withNames, Consumer<Throwable> consumePdfException) {
    exportReport(
        exportType,
        new UserBalanceReport(withNames),
        "Guthabenstände werden erstellt",
        consumePdfException);
  }

  public void exportKeyUserList(
      ExportTypes exportType, String sortOrder, Consumer<Throwable> consumePdfException) {
    exportReport(
        exportType,
        new KeyUserList(sortOrder),
        "Benutzer-Schlüssel-Liste wird erstellt",
        consumePdfException);
  }

  public void exportTransactionStatement(
      ExportTypes exportType,
      User user,
      StatementType statementType,
      boolean current,
      Consumer<Throwable> consumePdfException) {
    exportReport(
        exportType,
        new TransactionStatement(user, statementType, current),
        "Benutzer-Schlüssel-Liste wird erstellt",
        consumePdfException);
  }

  public void exportPermissionHolders(
      ExportTypes exportType, boolean withKeys, Consumer<Throwable> consumePdfException) {
    List<Permission> permissions =
        DBConnection.getEntityManager()
            .createQuery(
                "Select p from Permission p where not p.name in ('@IMPORT', '@APPLICATION', '@IN_RELATION_TO_OWN_USER'"
                    + (withKeys ? "" : ", '@Key_Permission', '@FULL_MEMBER' ,'@BASIC_ACCESS'")
                    + ")",
                Permission.class)
            .getResultList();
    exportReport(
        exportType,
        new PermissionHolders(permissions),
        "Rolleninhaber-Bericht wird erstellt",
        consumePdfException);
  }
}
