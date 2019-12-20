package kernbeisser.Windows.UserMenu;

import kernbeisser.DBEntitys.User;

public class UserMenuModel {
    private User owner;
    UserMenuModel(User owner) {
        this.owner=owner;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
