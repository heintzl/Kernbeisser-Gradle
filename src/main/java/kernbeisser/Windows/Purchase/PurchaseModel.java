package kernbeisser.Windows.Purchase;

import java.util.Collection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class PurchaseModel implements IModel<PurchaseController> {
  @Getter private final Purchase loaded;

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
