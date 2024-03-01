package kernbeisser.StartUp.LogIn;

import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class DBLogInController extends Controller<DBLogInView, DBLogInModel> {

  private boolean startUp = false;

  public DBLogInController() throws ClassIsSingletonException {
    super(new DBLogInModel());
  }

  public static DBLogInController openDBLogInController(boolean startUp)
      throws ClassIsSingletonException {
    DBLogInController dbLogInController = new DBLogInController();
    dbLogInController.startUp = startUp;
    return dbLogInController;
  }

  public boolean isStartUp() {
    return startUp;
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
