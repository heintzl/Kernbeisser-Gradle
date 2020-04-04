package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Model;

import java.util.Collection;
import java.util.HashMap;

public class ShoppingCartModel implements Model {
    private final double userValue;
    private final int userSurcharge;
    private HashMap<ShoppingItem,ShoppingItem> shoppingItems = new HashMap<>();


    ShoppingCartModel(double userValue, int userSurcharge) {
        this.userValue = userValue;
        this.userSurcharge = userSurcharge;
    }

    void addItem(ShoppingItem item) {
        ShoppingItem current = shoppingItems.get(item);
        if (current != null) {
            current.setItemMultiplier(item.getItemMultiplier() + current.getItemMultiplier());
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

    public int getUserSurcharge() {
        return userSurcharge;
    }
}
