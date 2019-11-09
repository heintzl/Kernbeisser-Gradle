package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntitys.User;

class CashierMenuController {
    private User user;
    CashierMenuController(User user){
        this.user=user;
    }
    public User getUser(){
        return user;
    }
}
