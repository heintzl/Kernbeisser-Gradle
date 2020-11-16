package kernbeisser.Windows.EditSurchargeTable;

import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.MVC.Controller;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class EditSurchargeTableController
    extends Controller<EditSurchargeTableView, EditSurchargeTableModel> {

  public EditSurchargeTableController(SurchargeGroup surchargeGroup, Mode mode) {
    super(
        new EditSurchargeTableModel(
            surchargeGroup == null ? new SurchargeGroup() : surchargeGroup, mode));
    if (mode == Mode.REMOVE) {
      Tools.delete(SurchargeGroup.class, surchargeGroup.getId());
    }
  }

  public void commit() {
    var view = getView();
    if (view.getObjectForm().applyMode(model.getMode())) {
      view.back();
    }
  }

  @Override
  public @NotNull EditSurchargeTableModel getModel() {
    return model;
  }

  @Override
  public void fillView(EditSurchargeTableView editSurchargeTableView) {
    var view = getView();
    view.setSuppliers(model.getAllSuppliers());
    view.getObjectForm().setSource(model.getSource());
  }

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
