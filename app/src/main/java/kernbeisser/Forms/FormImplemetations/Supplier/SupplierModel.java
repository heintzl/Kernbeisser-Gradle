package kernbeisser.Forms.FormImplemetations.Supplier;

import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Supplier_;
import kernbeisser.Windows.MVC.IModel;

public class SupplierModel implements IModel<SupplierController> {

  public boolean nameExists(String name) {
    return QueryBuilder.propertyWithThatValueExists(Supplier_.name, name);
  }

  public boolean shortNameExists(String name) {
    return QueryBuilder.propertyWithThatValueExists(Supplier_.shortName, name);
  }
}
