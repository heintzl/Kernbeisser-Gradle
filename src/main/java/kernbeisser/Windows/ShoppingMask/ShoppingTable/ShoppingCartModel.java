package kernbeisser.Windows.ShoppingMask.ShoppingTable;

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

  int addItem(ShoppingItem newItem) {
    return addItemAtIndex(newItem, shoppingItems.size());
  }

  int addItemBehind(ShoppingItem newItem, ShoppingItem behindItem) {
    return addItemAtIndex(newItem, shoppingItems.indexOf(getShoppingItem(behindItem)) + 1);
  }

  int addItemAtIndex(ShoppingItem newItem, int atIndex) {
    int index = atIndex;
    ShoppingItem existingItem = getShoppingItem(newItem);
    if (existingItem != null) {
      index = existingItem.getShoppingCartIndex();
      existingItem.setItemMultiplier(
          newItem.getItemMultiplier() + existingItem.getItemMultiplier());
    } else {
      newItem.setShoppingCartIndex(atIndex);
      shoppingItems.add(atIndex, newItem);
    }
    return index;
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
}
