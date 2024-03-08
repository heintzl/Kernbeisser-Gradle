package kernbeisser.Windows.LogIn.SimpleLogIn;

import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.IModel;

public class SimpleLogInModel implements IModel<SimpleLogInController> {

  void logIn(String username, char[] password) throws CannotLogInException, PermissionRequired {
    LogInModel.logIn(username, password);
  }
}
