package kernbeisser.Windows.UserMenu;

import kernbeisser.DBEntities.Purchase;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class UserMenuModel implements Model<UserMenuController> {


    public User getOwner() {
        return LogInModel.getLoggedIn();
    }

    public Collection<Purchase> getAllPurchase() {
        return getOwner().getAllPurchases();
    }
}
