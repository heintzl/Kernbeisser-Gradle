package kernbeisser.StartUp.LogIn;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.IController;
import org.jetbrains.annotations.NotNull;

public class DBLogInController implements IController<DBLogInView, DBLogInModel> {

  private DBLogInView view;
  private final DBLogInModel model;

  public DBLogInController() {
    this.model = new DBLogInModel();
  }

  @Override
  public @NotNull DBLogInModel getModel() {
    return model;
  }

  @Override
  public void fillUI() {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
