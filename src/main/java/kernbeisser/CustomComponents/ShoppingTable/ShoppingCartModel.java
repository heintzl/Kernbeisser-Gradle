package kernbeisser.CustomComponents.ShoppingTable;

import java.util.ArrayList;
import java.util.List;
import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.MVC.IModel;
import org.jetbrains.annotations.Nullable;

public class ShoppingCartModel implements IModel<ShoppingCartController> {
  private final double userValue;
  private final double userSurcharge;
  private final ArrayList<ShoppingItem> shoppingItems = new ArrayList<>();

  ShoppingCartModel(double userValue, double userSurcharge) {
    this.userValue = userValue;
    this.userSurcharge = userSurcharge;
  }

  ShoppingItem addItem(ShoppingItem newItem) {
    return addItemAtIndex(newItem, shoppingItems.size());
  }

  ShoppingItem addItemBehind(ShoppingItem newItem, ShoppingItem behindItem) {
    return addItemAtIndex(newItem, shoppingItems.indexOf(getShoppingItem(behindItem)) + 1);
  }

  boolean shoppingCartItemAlreadyExists(ShoppingItem item) {
    return getShoppingItem(item) != null;
  }

  ShoppingItem addItemAtIndex(ShoppingItem newItem, int atIndex) {
    ShoppingItem existingItem = getShoppingItem(newItem);
    if (existingItem != null) {
      existingItem.setItemMultiplier(
          newItem.getItemMultiplier() + existingItem.getItemMultiplier());
      return existingItem;
    } else {
      shoppingItems.add(atIndex, newItem);
      return newItem;
    }
  }

  public void increaseItemSpace() {
    this.shoppingItems.ensureCapacity(shoppingItems.size() + 2);
  }

  @Nullable
  private ShoppingItem getShoppingItem(ShoppingItem newItem) {
    ShoppingItem current = null;
    for (ShoppingItem item : shoppingItems) {
      if (newItem.equals(item)) {
        current = item;
      }
    }
    return current;
  }

  public double getUserValue() {
    return userValue;
  }

  List<ShoppingItem> getItems() {
    return shoppingItems;
  }

  public double getUserSurcharge() {
    return userSurcharge;
  }

  public double getTotalSum() {
    double sum = 0;
    for (ShoppingItem item : shoppingItems) {
      sum += item.getRetailPrice();
    }
    return sum;
  }
}
