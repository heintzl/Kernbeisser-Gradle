package kernbeisser.Windows.LogIn.SimpleLogIn;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.CannotLogInException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Menu.MenuController;
import kernbeisser.Windows.ViewContainers.SubWindow;
import lombok.var;
import org.jetbrains.annotations.NotNull;

public class SimpleLogInController extends Controller<SimpleLogInView, SimpleLogInModel> {

  public SimpleLogInController() {
    super(new SimpleLogInModel());
  }

  @Override
  public @NotNull SimpleLogInModel getModel() {
    return model;
  }

  @Override
  public void fillView(SimpleLogInView simpleLogInView) {}



  public void logIn() {
    var view = getView();
    try {
      model.logIn(view.getUsername(), view.getPassword());
      loadUserSettings();
      if (LogInModel.getLoggedIn().getLastPasswordChange().until(Instant.now(), ChronoUnit.DAYS)
              > Setting.FORCE_PASSWORD_CHANGE_AFTER.getIntValue()
          || LogInModel.getLoggedIn().isForcePasswordChange()) {
        new ChangePasswordController(LogInModel.getLoggedIn(), true)
            .openIn(new SubWindow(view.traceViewContainer()));
      } else {
        view.back();
        new MenuController().openTab();
      }
    } catch (CannotLogInException e) {
      view.accessDenied();
    } catch (PermissionRequired permissionRequired) {
      view.permissionRequired();
    }
  }

  private void loadUserSettings() {
    try {
      UIManager.setLookAndFeel(
          UserSetting.THEME.getEnumValue(Theme.class, LogInModel.getLoggedIn()).getLookAndFeel());
    } catch (UnsupportedLookAndFeelException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }
}
