package kernbeisser.Windows.AdminTools;

import java.util.Collection;
import kernbeisser.DBEntities.User;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.Users;
import kernbeisser.Windows.MVC.IModel;

public class AdminToolModel extends Users implements IModel<AdminToolController> {

  public Collection<User> getAllUsers() {
    // only admin access -> removes permission checks
    return Tools.getAllUnProxy(User.class);
  }
}
