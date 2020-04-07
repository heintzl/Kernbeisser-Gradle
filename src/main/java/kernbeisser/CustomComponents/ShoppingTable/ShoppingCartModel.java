package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Model;

import java.util.Collection;
import java.util.HashMap;

public class ShoppingCartModel implements Model {
    private final double userValue;
    private final double userSurcharge;
    private HashMap<ShoppingItem,ShoppingItem> shoppingItems = new HashMap<>();


    ShoppingCartModel(double userValue, double userSurcharge) {
        this.userValue = userValue;
        this.userSurcharge = userSurcharge;
    }

    void addItem(ShoppingItem item, boolean stack) {
        ShoppingItem current = shoppingItems.get(item);
        if (current != null) {
            if (stack) {
                current.setItemMultiplier(item.getItemMultiplier() + current.getItemMultiplier());
            } else {
                current.setItemNetPrice(current.getItemNetPrice() + item.getItemNetPrice());
            }
        } else {
            shoppingItems.put(item, item);
        }
    }

    public double getUserValue() {
        return userValue;
    }

    Collection<ShoppingItem> getItems() {
        return shoppingItems.values();
    }

    public double getUserSurcharge() {
        return userSurcharge;
    }
}
