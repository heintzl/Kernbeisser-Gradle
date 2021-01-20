package kernbeisser.StartUp.LogIn;

import kernbeisser.Config.Config;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Windows.MVC.IModel;

public class DBLogInModel implements IModel<DBLogInController> {

  DBLogInModel() {
    alreadyOpened = true;
  }

  public void close() {
    alreadyOpened = false;
  }

  public static transient boolean alreadyOpened = false;

  public boolean isServiceAvailable(Config.DBAccess access) {
    return DBConnection.checkValidDBAccess(access);
  }

  public boolean saveService(Config.DBAccess access) {
    if (DBConnection.tryLogIn(access)) {
      Config.getConfig().setDbAccess(access);
      Config.safeFile();
      return true;
    } else return false;
  }
}
