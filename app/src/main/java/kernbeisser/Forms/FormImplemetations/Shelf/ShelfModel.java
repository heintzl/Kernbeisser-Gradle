package kernbeisser.Forms.FormImplemetations.Shelf;

import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.TypeFields.ShelfField;
import kernbeisser.Windows.MVC.IModel;

public class ShelfModel implements IModel<ShelfController> {

  boolean shelfNoExists(int shelfNo) {
    return QueryBuilder.propertyWithThatValueExists(ShelfField.shelfNo, shelfNo);
  }

  boolean locationExists(String location) {
    if (location.isEmpty()) {
      return true;
    }
    return QueryBuilder.propertyWithThatValueExists(ShelfField.location, location);
  }
}
