package kernbeisser.Windows.EditSuppliers;

import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Forms.FormImplemetations.Supplier.SupplierController;
import kernbeisser.Forms.ObjectView.ObjectViewController;
import kernbeisser.Security.Key;
import rs.groump.PermissionKey;

public class EditSuppliers extends ObjectViewController<Supplier> {

  @Key(PermissionKey.ACTION_OPEN_EDIT_SUPPLIERS)
  public EditSuppliers() {
    super(
        "Lieferanten bearbeiten",
        new SupplierController(),
        Supplier::defaultSearch,
        true,
        Columns.create("Name", Supplier::getName),
        Columns.create("Kurzname", Supplier::getShortName),
        Columns.create("Zuschlag", Supplier::getDefaultSurcharge),
        Columns.create("Betreuer", Supplier::getKeeper),
        Columns.create("Straße", Supplier::getStreet),
        Columns.create("Ort", Supplier::getLocation),
        Columns.create("Email", Supplier::getEmail),
        Columns.create("Telefonnummer", Supplier::getPhoneNumber),
        Columns.create("Fax", Supplier::getFax));
  }
}
