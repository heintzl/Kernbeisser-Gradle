package kernbeisser.StartUp.LogIn;

import java.awt.event.ActionEvent;
import javax.swing.*;
import kernbeisser.Exeptions.ClassIsSingletonException;
import kernbeisser.Windows.MVC.Controller;
import org.jetbrains.annotations.NotNull;

public class DBLogInController extends Controller<DBLogInView, DBLogInModel> {

  private final Timer logInTimer = new Timer(1000, this::checkConnection);

  private void checkConnection(ActionEvent actionEvent) {
    getView().setConnectionValid(model.isServiceAvailable(getView().getDBAccess()));
  }

  public DBLogInController() throws ClassIsSingletonException {
    super(new DBLogInModel());
    logInTimer.start();
  }

  @Override
  public @NotNull DBLogInModel getModel() {
    return model;
  }

  @Override
  public void fillView(DBLogInView dbLogInView) {}

  void connectionChanged() {
    logInTimer.restart();
  }

  @Override
  protected void closed() {
    model.close();
  }

  void logIn() {
    model.saveService(getView().getDBAccess());
  }
}
