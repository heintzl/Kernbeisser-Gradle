package kernbeisser.StartUp.LogIn;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class DBLogInController extends Controller<DBLogInView, DBLogInModel> {

  public DBLogInController() {
    super(new DBLogInModel());
  }

  @Override
  public @NotNull DBLogInModel getModel() {
    return model;
  }

  @Override
  public void fillView(DBLogInView dbLogInView) {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }
}
