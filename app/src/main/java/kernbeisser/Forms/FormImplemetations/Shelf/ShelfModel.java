package kernbeisser.Forms.FormImplemetations.Shelf;

import kernbeisser.DBConnection.QueryBuilder;
import kernbeisser.DBEntities.Shelf_;
import kernbeisser.Windows.MVC.IModel;

public class ShelfModel implements IModel<ShelfController> {

  boolean shelfNoExists(int shelfNo) {
    return QueryBuilder.propertyWithThatValueExists(Shelf_.shelfNo, shelfNo);
  }

  boolean locationExists(String location) {
    if (location.isEmpty()) {
      return true;
    }
    return QueryBuilder.propertyWithThatValueExists(Shelf_.location, location);
  }
}
