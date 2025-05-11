package kernbeisser.Windows.AccountingReports;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import kernbeisser.DBEntities.Transaction;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.StatementType;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Exeptions.NoTransactionsFoundException;
import kernbeisser.Exeptions.handler.UnexpectedExceptionHandler;
import kernbeisser.Reports.*;
import kernbeisser.Windows.CashierShoppingMask.CashierShoppingMaskModel;
import kernbeisser.Windows.MVC.Controller;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class AccountingReportsController
    extends Controller<AccountingReportsView, AccountingReportsModel> {

  @Key(PermissionKey.ACTION_OPEN_ACCOUNTING_REPORTS)
  public AccountingReportsController() {
    super(new AccountingReportsModel());
  }

  ExportTypes[] getExportTypes() {
    return model.getExportTypes();
  }

  UserNameObfuscation[] getUserNameObfuscations() {
    return model.getUserNameObfuscations();
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
      UnexpectedExceptionHandler.showUnexpectedErrorWarning(t);
    }
  }

  private void exportReport(Report report, String message) {
    AccountingReportsView view = getView();
    ExportTypes exportType = view.getSelectedExportType();
    try {
      AccountingReportsModel.exportReport(
          exportType,
          report,
          message,
          view.getDuplexPrint(),
          e -> consumePdfException(e, exportType));
    } catch (UnsupportedOperationException e) {
      view.messageNotImplemented(exportType);
    }
  }

  public void exportTillroll(Instant startDate, Instant endDate) {
    if (!startDate.isBefore(endDate)) {
      getView().messageDateValues();
      return;
    }
    exportReport(
        new TillrollReport(startDate, endDate.plus(1, ChronoUnit.DAYS)), "Erstelle Bonrolle");
  }

  public void exportAccountingReport(long reportNo, UserNameObfuscation withNames) {
    AccountingReportsView view = getView();
    if (reportNo == Transaction.getLastReportNo() + 1) {
      try {
        CashierShoppingMaskModel.printAccountingReports(
            Transaction.getUnreportedTransactions(), view::messageNoAccountingReport);
      } catch (NoTransactionsFoundException e) {
        view.messageEmptyReportNo(reportNo);
      }
    } else {
      try {
        List<Transaction> reportTransactions =
            Transaction.getTransactionsByReportNo(reportNo).stream()
                .filter(t -> t.isAccountingReportTransaction() || t.isPurchase())
                .collect(Collectors.toList());
        if (reportTransactions.isEmpty()) throw new NoTransactionsFoundException();
        AccountingReportsModel.exportAccountingReports(
            reportTransactions, reportNo, withNames, view.getDuplexPrint());
      } catch (NoTransactionsFoundException e) {
        view.messageEmptyReportNo(reportNo);
      }
    }
  }

  public void exportUserBalance(long reportNo, boolean userBalanceWithNames) {
    exportReport(
        new UserBalanceReport(reportNo, userBalanceWithNames), "Erstelle Guthabenstände");
  }

  public void exportKeyUserList(String sortOrder) {
    exportReport(new KeyUserList(sortOrder), "Erstelle Benutzer-Schlüssel");
  }

  public void exportTransactionStatement(User user, StatementType statementType, boolean current) {
    exportReport(
        new TransactionStatement(user, statementType, current), "Erstelle Kontoauszug");
  }

  public void exportPermissionHolders(boolean permissionHoldersWithKeys) {
    exportReport(
        new PermissionHolders(permissionHoldersWithKeys), "Erstelle Rolleninhaber-Bericht");
  }

  public void exportLossAnalysis(Instant startDate, Instant endDate) {
    if (!startDate.isBefore(endDate)) {
      getView().messageDateValues();
      return;
    }
    exportReport(
        new LossAnalysisReport(startDate, endDate.plus(1, ChronoUnit.DAYS)),
        "Erstelle Schwundanalyse");
  }

  @Override
  public void fillView(AccountingReportsView accountingReportsView) {
    accountingReportsView.setUser(User.getAllUserFullNames(true, true));
    accountingReportsView.getOptAccountingReport().doClick();
  }
}
