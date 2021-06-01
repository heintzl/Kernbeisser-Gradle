package kernbeisser.Windows.AccountingReports;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Reports.*;
import kernbeisser.Security.Key;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;

public class AccountingReportsController
    extends Controller<AccountingReportsView, AccountingReportsModel> {

  @Key(PermissionKey.ACTION_OPEN_ACCOUNTING_REPORTS)
  public AccountingReportsController() {
    super(new AccountingReportsModel());
  }

  ExportTypes[] getExportTypes() {
    return model.getExportTypes();
  }

  Collection<String> getUserKeySortOrders() {
    return model.getUserKeySortOrders();
  }

  private void consumePdfException(Throwable e, ExportTypes exportType) {
    try {
      try {
        throw e;
      } catch (RuntimeException r) {
        throw r.getCause();
      }
    } catch (IncorrectInput i) {
      getView().messageNoItems(e.getMessage());
    } catch (UnsupportedOperationException u) {
      getView().messageNotImplemented(exportType);
    } catch (Throwable t) {
      Tools.showUnexpectedErrorWarning(t);
    }
  }

  private void exportReport(Report report, String message) {
    var view = getView();
    var exportType = view.getExportType();
    try {
      AccountingReportsModel.exportReport(
          exportType, report, message, e -> consumePdfException(e, exportType));
    } catch (UnsupportedOperationException e) {
      view.messageNotImplemented(exportType);
    }
  }

  public void exportTillroll(Instant startDate, Instant endDate) {
    exportReport(
        new TillrollReport(startDate, endDate.plus(1, ChronoUnit.DAYS)), "Bonrolle wird erstellt");
  }

  public void exportAccountingReport(
      Optional<Purchase> startBon, Optional<Purchase> endBon, boolean withNames) {
    var view = getView();
    long startBonNo = startBon.map(Purchase::getId).orElse(0L);
    long endBonNo = endBon.map(Purchase::getId).orElse(Purchase.getLastBonNo());
    if (startBonNo > endBonNo) {
      view.messageBonValues();
      return;
    }
    exportReport(
        new AccountingReport(0, startBonNo, endBonNo, withNames),
        "Ladendienst Endabrechnung wird erstellt");
  }

  public void exportUserBalance(boolean userBalanceWithNames) {
    exportReport(new UserBalanceReport(userBalanceWithNames), "Guthabenstände werden erstellt");
  }

  public void exportKeyUserList(String sortOrder) {
    exportReport(new KeyUserList(sortOrder), "Benutzer-Schlüssel-Liste wird erstellt");
  }

  public void exportTransactionStatement(User user, StatementType statementType, boolean current) {
    exportReport(
        new TransactionStatement(user, statementType, current), "Kontoauszug wird erstellt");
  }

  public void exportPermissionHolders(boolean permissionHoldersWithKeys) {
    exportReport(
        new PermissionHolders(permissionHoldersWithKeys), "Rolleninhaber-Bericht wird erstellt");
  }

  @Override
  public void fillView(AccountingReportsView accountingReportsView) {
    accountingReportsView.setUser(User.getAllUserFullNames(true));
    accountingReportsView.setBons(Purchase.getAll(null));
    accountingReportsView.getOptAccountingReport().doClick();
  }
}
