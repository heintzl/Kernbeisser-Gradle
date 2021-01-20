package kernbeisser.StartUp.LogIn;

import kernbeisser.Config.Config;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Exeptions.ClassIsSingletonException;
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
    Config.DBAccess before = Config.getConfig().getDbAccess();
    try {
      Config.getConfig().setDbAccess(access);
      if (DBConnection.tryLogIn(access)) {
        Config.safeFile();
        return true;
      } else {
        Config.getConfig().setDbAccess(before);
        return false;
      }
    } catch (Throwable t) {
      Config.getConfig().setDbAccess(before);
      throw t;
    }
  }
}
