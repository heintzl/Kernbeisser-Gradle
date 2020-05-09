package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Model;

import javax.transaction.NotSupportedException;
import java.util.Collection;
import java.util.HashMap;

public class ShoppingCartModel implements Model<ShoppingCartController> {
    private final double userValue;
    private final double userSurcharge;
    private final HashMap<ShoppingItem,ShoppingItem> shoppingItems = new HashMap<>();


    ShoppingCartModel(double userValue, double userSurcharge) {
        this.userValue = userValue;
        this.userSurcharge = userSurcharge;
    }

    void addItem(ShoppingItem item, boolean piece) {
        ShoppingItem current = shoppingItems.get(item);
        if (current != null) {
            if (piece) {
                current.setItemMultiplier(item.getItemMultiplier() + current.getItemMultiplier());
            } else {
                try {
                    current.addToRetailPrice(item.getRetailPrice());
                } catch (NotSupportedException e) {
                    e.printStackTrace();
                }
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
