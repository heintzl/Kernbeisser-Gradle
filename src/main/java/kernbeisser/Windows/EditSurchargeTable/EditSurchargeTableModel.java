package kernbeisser.Windows.EditSurchargeTable;

import java.util.Collection;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Enums.Mode;
import kernbeisser.Windows.MVC.IModel;

public class EditSurchargeTableModel implements IModel<EditSurchargeTableController> {
  private final SurchargeGroup source;
  private final Mode mode;

  public EditSurchargeTableModel(SurchargeGroup surchargeGroup, Mode mode) {
    this.source = surchargeGroup;
    this.mode = mode;
  }

  Collection<Supplier> getAllSuppliers() {
    return Supplier.getAll(null);
  }

  public SurchargeGroup getSource() {
    return source;
  }

  public Mode getMode() {
    return mode;
  }
}
