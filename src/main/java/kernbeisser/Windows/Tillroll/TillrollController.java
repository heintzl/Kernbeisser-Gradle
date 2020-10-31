package kernbeisser.Windows.Tillroll;

import java.awt.print.PrinterAbortException;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class TillrollController extends Controller<TillrollView, TillrollModel> {

  public TillrollController() {
    super(new TillrollModel());
  }

  ExportTypes[] getExportTypes() {
    return model.getExportTypes();
  }

  public void exportTillroll(ExportTypes exportType, int days) {
    try {
      model.exportTillroll(exportType, days);
      getView().back();
    } catch (UnsupportedOperationException e) {
      getView().messageNotImplemented(exportType);
    } catch (IncorrectInput e) {
      getView().messageNoItems(e.getMessage());
    } catch (JRException e) {
      if (ExceptionUtils.indexOfType(e.getCause(), PrinterAbortException.class) != -1) {
        Tools.showPrintAbortedWarning(e, true);
      } else {
        Tools.showUnexpectedErrorWarning(e);
      }
    }
  }

  public void exportAccountingReport(ExportTypes exportType, int startBon, int endBon) {
    try {
      model.exportAccountingReport(exportType, startBon, endBon);
      getView().back();
    } catch (UnsupportedOperationException e) {
      getView().messageNotImplemented(exportType);
    } catch (InvalidVATValueException e) {
      getView().messageNoItems(e.getMessage());
    } catch (JRException e) {
      if (ExceptionUtils.indexOfType(e.getCause(), PrinterAbortException.class) != -1) {
        Tools.showPrintAbortedWarning(e, true);
      } else {
        Tools.showUnexpectedErrorWarning(e);
      }
    }
  }

  @Override
  public void fillView(TillrollView tillrollView) {}
}
