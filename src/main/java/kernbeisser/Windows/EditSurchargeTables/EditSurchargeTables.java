package kernbeisser.Windows.EditSurchargeTables;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Windows.EditSurchargeTable.EditSurchargeTableController;
import kernbeisser.Windows.ObjectView.ObjectViewController;

public class EditSurchargeTables extends ObjectViewController<SurchargeTable> {
  public EditSurchargeTables() {
    super(
        "Zuschlagstabellen bearbeiten",
        EditSurchargeTableController::new,
        SurchargeTable::defaultSearch,
        true,
        Column.create("Lieferant", SurchargeTable::getSupplier),
        Column.create("Von", SurchargeTable::getFrom_number),
        Column.create("Bis", SurchargeTable::getTo_number),
        Column.create("Name", SurchargeTable::getDescription),
        Column.create("Zuschlag", SurchargeTable::getSurcharge));
  }
}
