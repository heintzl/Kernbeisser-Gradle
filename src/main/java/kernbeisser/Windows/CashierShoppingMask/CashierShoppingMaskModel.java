package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.DBEntities.User;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class CashierShoppingMaskModel implements Model<CashierShoppingMaskController> {
    public Collection<User> getUsers(String search) {
        return User.defaultSearch(search, 500);
    }
}
