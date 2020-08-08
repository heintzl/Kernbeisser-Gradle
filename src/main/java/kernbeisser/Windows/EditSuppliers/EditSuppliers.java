package kernbeisser.Windows.EditSuppliers;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Windows.EditSupplier.EditSupplierController;
import kernbeisser.Windows.ObjectView.ObjectViewController;

public class EditSuppliers extends ObjectViewController<Supplier> {
  public EditSuppliers() {
    super(
        EditSupplierController::new,
        Supplier::defaultSearch,
        true,
        Column.create("Name", Supplier::getName),
        Column.create("Kurzname", Supplier::getShortName),
        Column.create("Zuschlag", Supplier::getSurcharge),
        Column.create("Betreuer", Supplier::getKeeper),
        Column.create("Adresse", Supplier::getAddress),
        Column.create("Email", Supplier::getEmail),
        Column.create("Telefonnummer", Supplier::getPhoneNumber),
        Column.create("Fax", Supplier::getFax));
  }
}
