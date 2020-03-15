package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Model;

import java.util.Collection;
import java.util.HashMap;

public class ShoppingCartModel implements Model {
    private final int userValue;
    private final int userSurcharge;
    private HashMap<ShoppingItem,ShoppingItem> shoppingItems = new HashMap<>();


    ShoppingCartModel(int userValue, int userSurcharge) {
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

    public int getUserValue() {
        return userValue;
    }

    Collection<ShoppingItem> getItems() {
        return shoppingItems.values();
    }

    public int getUserSurcharge() {
        return userSurcharge;
    }
}
