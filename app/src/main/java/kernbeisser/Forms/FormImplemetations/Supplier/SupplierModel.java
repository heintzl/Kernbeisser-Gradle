package kernbeisser.Forms.FormImplemetations.Supplier;

import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.TypeFields.SupplierField;
import kernbeisser.Windows.MVC.IModel;

public class SupplierModel implements IModel<SupplierController> {

  public boolean nameExists(String name) {
    return QueryBuilder.propertyWithThatValueExists(SupplierField.name, name);
  }

  public boolean shortNameExists(String name) {
    return QueryBuilder.propertyWithThatValueExists(SupplierField.shortName, name);
  }
}
