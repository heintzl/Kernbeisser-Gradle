package kernbeisser.Windows.Tillroll;

import java.awt.print.PrinterAbortException;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Exeptions.InvalidVATValueException;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IController;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

public class TillrollController implements IController<TillrollView, TillrollModel> {
  private final TillrollModel model;
  private TillrollView view;

  public TillrollController() {
    model = new TillrollModel();
  }

  ExportTypes[] getExportTypes() {
    return model.getExportTypes();
  }

  @Override
  public @NotNull TillrollModel getModel() {
    return null;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  public void exportTillroll(ExportTypes exportType, int days) {
    try {
      model.exportTillroll(exportType, days);
      view.back();
    } catch (UnsupportedOperationException e) {
      view.messageNotImplemented(exportType);
    } catch (IncorrectInput e) {
      view.messageNoItems(e.getMessage());
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
      view.back();
    } catch (UnsupportedOperationException e) {
      view.messageNotImplemented(exportType);
    } catch (InvalidVATValueException e) {
      view.messageNoItems(e.getMessage());
    } catch (JRException e) {
      if (ExceptionUtils.indexOfType(e.getCause(), PrinterAbortException.class) != -1) {
        Tools.showPrintAbortedWarning(e, true);
      } else {
        Tools.showUnexpectedErrorWarning(e);
      }
    }
  }
}
