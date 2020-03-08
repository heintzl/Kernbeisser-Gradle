package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Model;

import java.util.Collection;
import java.util.HashMap;

public class ShoppingCartModel implements Model {
    private HashMap<ShoppingItem,ShoppingItem> shoppingItems = new HashMap<>();
    private final int userValue;
    private final int userSurcharge;


    ShoppingCartModel(int userValue,int userSurcharge) {
        this.userValue = userValue;
        this.userSurcharge = userSurcharge;
    }

    void addItem(ShoppingItem item) {
        ShoppingItem current = shoppingItems.get(item);
        if (current != null) {
            current.setItemAmount(item.getItemAmount() + current.getItemAmount());
        } else {
            shoppingItems.put(item,item);
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
