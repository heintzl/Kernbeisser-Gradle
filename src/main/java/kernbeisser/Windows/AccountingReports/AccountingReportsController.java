package kernbeisser.Windows.AccountingReports;

import kernbeisser.Enums.ExportTypes;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;

public class AccountingReportsController
    extends Controller<AccountingReportsView, AccountingReportsModel> {

  public AccountingReportsController() {
    super(new AccountingReportsModel());
  }

  ExportTypes[] getExportTypes() {
    return model.getExportTypes();
  }

  public void exportTillroll(ExportTypes exportType, int days) {
    var view = getView();
    model.exportTillroll(exportType, days, (e) -> consumePdfException(e, exportType));
    view.back();
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

  public void exportAccountingReport(ExportTypes exportType, int startBon, int endBon) {
    var view = getView();
    try {
      model.exportAccountingReport(
          exportType, startBon, endBon, (e) -> consumePdfException(e, exportType));
      view.back();
    } catch (UnsupportedOperationException e) {
      view.messageNotImplemented(exportType);
    }
  }

  public void exportUserBalance(ExportTypes exportType, boolean selected) {
    var view = getView();
    try {
      model.exportUserBalance(exportType, selected, (e) -> consumePdfException(e, exportType));
      view.back();
    } catch (UnsupportedOperationException e) {
      view.messageNotImplemented(exportType);
    }
  }

  @Override
  public void fillView(AccountingReportsView accountingReportsView) {
    getView().getOptLadendienstEndabrechnung().setSelected(true);
  }
}
