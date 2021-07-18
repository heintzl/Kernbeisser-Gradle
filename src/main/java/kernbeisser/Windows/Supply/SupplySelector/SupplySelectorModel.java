package kernbeisser.Windows.Supply.SupplySelector;

import java.util.Collection;
import java.util.function.Consumer;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.MVC.IModel;
import lombok.Getter;

public class SupplySelectorModel implements IModel<SupplySelectorController> {

  @Getter private final Consumer<Collection<ShoppingItem>> consumer;

  public SupplySelectorModel(Consumer<Collection<ShoppingItem>> consumer) {

    this.consumer = consumer;
  }
}
