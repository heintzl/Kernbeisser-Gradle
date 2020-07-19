package kernbeisser.Windows.UserMenu;

import java.util.Collection;
import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Model;

public class UserMenuModel implements Model<UserMenuController> {

  public User getOwner() {
    return LogInModel.getLoggedIn();
  }

  public Collection<Purchase> getAllPurchase() {
    return getOwner().getAllPurchases();
  }
}
