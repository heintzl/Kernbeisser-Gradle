package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class EditSurchargeTableController
    extends Controller<EditSurchargeTableView, EditSurchargeTableModel> {

  public EditSurchargeTableController(SurchargeTable surchargeTable, Mode mode) {
    super(
        new EditSurchargeTableModel(
            surchargeTable == null ? new SurchargeTable() : surchargeTable, mode));
    if (mode == Mode.REMOVE) {
      Tools.delete(SurchargeTable.class, surchargeTable.getId());
    }
  }

  public void commit() {
    if (getView().getObjectForm().applyMode(model.getMode())) {
      getView().back();
    }
  }

  @Override
  public @NotNull EditSurchargeTableModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditSurchargeTableView editSurchargeTableView) {
    getView().setSuppliers(model.getAllSuppliers());
    getView().getObjectForm().setSource(model.getSource());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
