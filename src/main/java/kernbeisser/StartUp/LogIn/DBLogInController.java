package kernbeisser.StartUp.LogIn;

import static kernbeisser.Enums.PermissionKey.ACTION_OPEN_DB_LOG_IN;

import javax.swing.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class DBLogInController extends Controller<DBLogInView, DBLogInModel> {

  public DBLogInController() throws ClassIsSingletonException {
    super(new DBLogInModel());
  }

  @Override
  public @NotNull DBLogInModel getModel() {
    return model;
  }

  @Override
  public void fillView(DBLogInView dbLogInView) {}

  @Override
  protected void closed() {
    model.close();
  }

  void logIn() {
    model.saveService(getView().getDBAccess());
    getView().connectionRefused();
  }

  @Override
  @StaticAccessPoint
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[] {ACTION_OPEN_DB_LOG_IN};
  }
}
