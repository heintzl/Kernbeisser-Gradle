package kernbeisser.StartUp.LogIn;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class DBLogInController implements Controller<DBLogInView, DBLogInModel> {

  private final DBLogInView view;
  private final DBLogInModel model;

  public DBLogInController() {
    this.model = new DBLogInModel();
    this.view = new DBLogInView(this);
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
