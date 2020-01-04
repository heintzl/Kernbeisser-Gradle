package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class CashierShoppingMaskModel implements Model {
    private User seller;
    CashierShoppingMaskModel(User seller){
        this.seller=seller;
    }
    public User getSeller() {
        return seller;
    }
    Collection<User> getAllUser(){
        return User.getAll(null);
    }
}
