package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class EditSurchargeTableController
    implements Controller<EditSurchargeTableView, EditSurchargeTableModel> {

  private final EditSurchargeTableModel model;
  private EditSurchargeTableView view;

  public EditSurchargeTableController(SurchargeTable surchargeTable, Mode mode) {
    this.model =
        new EditSurchargeTableModel(
            surchargeTable == null ? new SurchargeTable() : surchargeTable, mode);
    if (mode == Mode.REMOVE) {
      model.doAction(surchargeTable);
    }
  }

  public void commit() {
    try {
      if (model.doAction(view.getObjectForm().getData())) {
        view.back();
      }
    } catch (CannotParseException e) {
      view.incorrectInput();
      view.getObjectForm().markErrors();
    }
  }

  @Override
  public @NotNull EditSurchargeTableModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {
    view.setSuppliers(model.getAllSuppliers());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
