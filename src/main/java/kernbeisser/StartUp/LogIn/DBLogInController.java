package kernbeisser.StartUp.LogIn;

import kernbeisser.Exeptions.ClassIsSingletonException;
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
}
