package kernbeisser.StartUp.LogIn;

import kernbeisser.Config.Config;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Main;
import kernbeisser.Windows.MVC.IModel;

public class DBLogInModel implements IModel<DBLogInController> {

  DBLogInModel() throws ClassIsSingletonException {
    if (alreadyOpened) throw new ClassIsSingletonException();
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
    if (DBConnection.checkValidDBAccess(access)) {
      Config.getConfig().setDbAccess(access);
      Config.safeFile();
      Main.restart(null);
      return true;
    }
    return false;
  }
}
