package kernbeisser.CustomComponents.ShoppingTable;

import kernbeisser.DBEntities.ShoppingItem;
import kernbeisser.Windows.Model;

import java.util.Collection;
import java.util.HashMap;

public class ShoppingCartModel implements Model {
    private HashMap<ShoppingItem,ShoppingItem> shoppingItems = new HashMap<>();
    private final int userValue;


    ShoppingCartModel(int userValue){
        this.userValue = userValue;
    }

    void addItem(ShoppingItem item){
        ShoppingItem current = shoppingItems.get(item);
        if(current!=null){
            current.setAmount(item.getAmount()+current.getAmount());
        }else {
            shoppingItems.put(item,item);
        }
    }

    public int getUserValue() {
        return userValue;
    }

    Collection<ShoppingItem> getItems(){
        return shoppingItems.values();
    }
}
