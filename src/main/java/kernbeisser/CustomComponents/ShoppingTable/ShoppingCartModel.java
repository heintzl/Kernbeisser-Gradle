package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Model;
import org.jetbrains.annotations.Nullable;

import javax.transaction.NotSupportedException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartModel implements Model<ShoppingCartController> {
    private final double userValue;
    private final double userSurcharge;
    private final ArrayList<ShoppingItem> shoppingItems = new ArrayList<>();


    ShoppingCartModel(double userValue, double userSurcharge) {
        this.userValue = userValue;
        this.userSurcharge = userSurcharge;
    }

    void addItem(ShoppingItem newItem, boolean piece) {
        ShoppingItem existingItem = getShoppingItem(newItem);
        if (existingItem != null) {
            if (piece) {
                existingItem.setItemMultiplier(newItem.getItemMultiplier() + existingItem.getItemMultiplier());
            } else {
                try {
                    existingItem.addToRetailPrice(newItem.getRetailPrice());
                } catch (NotSupportedException e) {
                    Tools.showUnexpectedErrorWarning(e);
                }
            }
        } else {
            shoppingItems.add(newItem);
            newItem.setShoppingCartIndex(shoppingItems.size() - 1);
        }
    }

    @Nullable
    private ShoppingItem getShoppingItem(ShoppingItem newItem) {
        ShoppingItem current = null;
        int hashCode = newItem.hashCode();
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
