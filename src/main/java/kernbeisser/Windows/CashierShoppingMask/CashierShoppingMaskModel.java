package kernbeisser.Windows.CashierShoppingMask;

import java.util.Collection;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.Model;

public class CashierShoppingMaskModel implements Model<CashierShoppingMaskController> {
  public Collection<User> getUsers(String search) {
    return User.defaultSearch(search, 500);
  }
}
