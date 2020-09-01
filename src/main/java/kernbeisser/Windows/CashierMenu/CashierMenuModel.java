package kernbeisser.Windows.CashierMenu;

import kernbeisser.DBEntities.User;
import kernbeisser.Windows.MVC.IModel;

public class CashierMenuModel implements IModel<CashierMenuController> {
  private final User user;

  CashierMenuModel(User user) {
    this.user = user;
  }

  User getOwner() {
    return user;
  }
}
