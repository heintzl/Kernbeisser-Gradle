package kernbeisser.Windows.Tillroll;

import javax.swing.*;
import kernbeisser.Enums.ExportTypes;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.IncorrectInput;
import kernbeisser.Windows.MVC.IController;
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
    }
  }
}
