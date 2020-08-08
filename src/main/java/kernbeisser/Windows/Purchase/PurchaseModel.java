package kernbeisser.Windows.Purchase;

import java.util.Collection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.MVC.Model;

public class PurchaseModel implements Model<PurchaseController> {
  private final Purchase loaded;

  PurchaseModel(Purchase loaded) {
    this.loaded = loaded;
  }

  Collection<ShoppingItem> getAllItems() {
    return loaded.getAllItems();
  }

  double getSum() {
    return loaded.getSum();
  }

  public Purchase getLoaded() {
    return loaded;
  }
}
