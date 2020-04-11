package kernbeisser.Windows.EditSurchargeTables;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Windows.EditSurchargeTable.EditSurchargeTableController;
import kernbeisser.Windows.ObjectView.ObjectViewController;

public class EditSurchargeTables extends ObjectViewController<SurchargeTable> {
    public EditSurchargeTables() {
        super(EditSurchargeTableController::new, SurchargeTable::defaultSearch,
              Column.create("Liefernat", SurchargeTable::getSupplier),
              Column.create("Von", SurchargeTable::getFrom),
              Column.create("Bis", SurchargeTable::getTo),
              Column.create("Name", SurchargeTable::getName),
              Column.create("Zuschlag", SurchargeTable::getSurcharge)
        );
    }
}
