package kernbeisser.Windows.EditSuppliers;

import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Forms.FormImplemetations.Supplier.SupplierController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Security.Requires;

@Requires(PermissionKey.ACTION_OPEN_EDIT_SUPPLIERS)
public class EditSuppliers extends ObjectViewController<Supplier> {
  public EditSuppliers() {
    super(
        "Lieferanten bearbeiten",
        new SupplierController(),
        Supplier::defaultSearch,
        true,
        Column.create("Name", Supplier::getName),
        Column.create("Kurzname", Supplier::getShortName),
        Column.create("Zuschlag", Supplier::getDefaultSurcharge),
        Column.create("Betreuer", Supplier::getKeeper),
        Column.create("Straße", Supplier::getStreet),
        Column.create("Ort", Supplier::getLocation),
        Column.create("Email", Supplier::getEmail),
        Column.create("Telefonnummer", Supplier::getPhoneNumber),
        Column.create("Fax", Supplier::getFax));
  }
}
