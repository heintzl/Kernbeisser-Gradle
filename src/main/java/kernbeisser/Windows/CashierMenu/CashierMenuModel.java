package kernbeisser.Windows.CashierMenu;


import kernbeisser.DBEntitys.User;
import kernbeisser.Windows.Model;

class CashierMenuModel implements Model {
    private User user;
    CashierMenuModel(User user){
        this.user=user;
    }
    User getUser() {
        return user;
    }
}
