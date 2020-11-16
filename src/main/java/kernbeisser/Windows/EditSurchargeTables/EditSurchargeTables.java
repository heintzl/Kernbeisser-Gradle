package kernbeisser.Windows.EditSurchargeTables;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.SurchargeGroup;
import kernbeisser.Windows.EditSurchargeTable.EditSurchargeTableController;
import kernbeisser.Windows.ObjectView.ObjectViewController;

public class EditSurchargeTables extends ObjectViewController<SurchargeGroup> {
  public EditSurchargeTables() {
    super(
        "Zuschlagstabellen bearbeiten",
        EditSurchargeTableController::new,
        SurchargeGroup::defaultSearch,
        true,
        Column.create("Liefernat", SurchargeGroup::getSupplier),
        Column.create("Zuschlag", SurchargeGroup::getSurcharge));
  }
}
