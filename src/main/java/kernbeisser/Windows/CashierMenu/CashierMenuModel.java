package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.Model;

public class CashierMenuModel implements Model<CashierMenuController> {
  private final User user;

  CashierMenuModel(User user) {
    this.user = user;
  }

  User getUser() {
    return user;
  }
}
