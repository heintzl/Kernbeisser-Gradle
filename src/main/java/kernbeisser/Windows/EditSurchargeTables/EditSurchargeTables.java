package kernbeisser.Windows.EditSurchargeTables;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.SurchargeTable;
import kernbeisser.Main;
import kernbeisser.Windows.EditSurchargeTable.EditSurchargeTableController;
import kernbeisser.Windows.ObjectView.ObjectViewController;
import kernbeisser.Windows.Window;

import javax.swing.*;

public class EditSurchargeTables extends ObjectViewController<SurchargeTable> {
    public EditSurchargeTables(Window current) {
        super(current, EditSurchargeTableController::new, SurchargeTable::defaultSearch,
              Column.create("Liefernat", SurchargeTable::getSupplier),
              Column.create("Von", SurchargeTable::getFrom),
              Column.create("Bis", SurchargeTable::getTo),
              Column.create("Name", SurchargeTable::getName),
              Column.create("Zuschlag", SurchargeTable::getSurcharge)
        );
    }

    public static void main(String[] args)
            throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException,
                   IllegalAccessException {
        Main.buildEnvironment();
        new EditSurchargeTables(null);
    }
}
