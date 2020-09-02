package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class EditSurchargeTableController
    implements IController<EditSurchargeTableView, EditSurchargeTableModel> {

  private final EditSurchargeTableModel model;
  private EditSurchargeTableView view;

  public EditSurchargeTableController(SurchargeTable surchargeTable, Mode mode) {
    this.model =
        new EditSurchargeTableModel(
            surchargeTable == null ? new SurchargeTable() : surchargeTable, mode);
    if (mode == Mode.REMOVE) {
      Tools.delete(SurchargeTable.class, surchargeTable.getStid());
    }
  }

  public void commit() {
    if (view.getObjectForm().applyMode(model.getMode())) {
      view.back();
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
