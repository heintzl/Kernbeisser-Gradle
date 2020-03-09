package kernbeisser.Windows.CashierShoppingMask;

import kernbeisser.DBEntities.User;

import java.util.Collection;

public class CashierShoppingMaskModel {
    public Collection<User> getUsers(String search) {
        return User.defaultSearch(search,500);
    }
}
