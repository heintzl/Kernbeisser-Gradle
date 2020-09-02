package kernbeisser.Windows.EditSurchargeTable;

import java.util.Collection;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MVC.IModel;

public class EditSurchargeTableModel implements IModel<EditSurchargeTableController> {
  private final SurchargeTable source;
  private final Mode mode;

  public EditSurchargeTableModel(SurchargeTable surchargeTable, Mode mode) {
    this.source = surchargeTable;
    this.mode = mode;
  }

  Collection<Supplier> getAllSuppliers() {
    return Supplier.getAll(null);
  }

  public SurchargeTable getSource() {
    return source;
  }

  public Mode getMode() {
    return mode;
  }
}
