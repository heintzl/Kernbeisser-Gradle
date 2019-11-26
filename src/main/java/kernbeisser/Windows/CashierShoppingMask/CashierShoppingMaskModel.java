package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.ShoppingMask.ShoppingMask;

import java.util.ArrayList;
import java.util.Collection;

public class CashierShoppingMaskModel implements Model {
    private ArrayList<ShoppingMask> openSessions = new ArrayList<>();
    private User seller;
    CashierShoppingMaskModel(User seller){
        this.seller=seller;
    }
    Collection<ShoppingMask> getOpenSessions(){
        return openSessions;
    }

    public User getSeller() {
        return seller;
    }
}
