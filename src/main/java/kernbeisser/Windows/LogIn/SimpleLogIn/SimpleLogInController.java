package kernbeisser.Windows.LogIn.SimpleLogIn;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import kernbeisser.DBEntities.User;
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
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
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

  public boolean shouldForcePasswordChange(User user) {
    return user.getLastPasswordChange().until(Instant.now(), ChronoUnit.DAYS)
            > Setting.FORCE_PASSWORD_CHANGE_AFTER.getIntValue()
        || user.isForcePasswordChange();
  }

  public void logIn() {
    var view = getView();
    try {
      model.logIn(view.getUsername(), view.getPassword());
      loadUserSettings();
      if (shouldForcePasswordChange(LogInModel.getLoggedIn())) {
        new ChangePasswordController(LogInModel.getLoggedIn(), true)
            .openIn(new SubWindow(view.traceViewContainer()));
      } else {
        new MenuController().openTab();
        view.back();
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

  @Override
  protected void closed() {
    // shutdowns application if tab gets closed without any other other tab is opened
    // 1 because the model removes the tab from the model after it notified the tab itself
    if (TabbedPaneModel.MAIN_PANEL.getModel().size() == 1) {
      System.exit(0);
    }
  }
}
