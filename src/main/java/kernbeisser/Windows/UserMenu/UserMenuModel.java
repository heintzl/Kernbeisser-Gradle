package kernbeisser.Windows.UserMenu;

import kernbeisser.DBEntitys.Purchase;
import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Model;

import java.util.Collection;

public class UserMenuModel implements Model {
    private User owner;
    UserMenuModel(User owner) {
        this.owner=owner;
    }

    public User getOwner() {
        return owner;
    }

    public Collection<Purchase> getAllPurchase(){
        return owner.getAllPurchases();
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
