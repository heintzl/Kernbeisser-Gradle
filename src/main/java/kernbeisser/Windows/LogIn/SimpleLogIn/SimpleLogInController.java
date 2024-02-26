package kernbeisser.Windows.LogIn.SimpleLogIn;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.DBEntities.UserGroup;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.*;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Useful.UiTools;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.Menu.MenuController;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import kernbeisser.Windows.ViewContainers.SubWindow;
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
    SimpleLogInView view = getView();
    if (view.getUsername().toLowerCase(Locale.ROOT).equals("admin")) {
      PermissionConstants.cleanAdminPermission(User.getByUsername("Admin"));
    }
    try {
      model.logIn(view.getUsername(), view.getPassword());
    } catch (CannotLogInException e) {
      view.accessDenied();
      return;
    } catch (PermissionRequired permissionRequired) {
      view.permissionRequired();
      return;
    }

    view.indicateProgress(true);
    new Thread(
            () -> {
              loadUserSettings();
              User.refreshActivity();
              try {
                User.checkAdminConsistency();
              } catch (InvalidValue e) {
                Tools.showUnexpectedErrorWarning(e);
              }
              try {
                UserGroup.checkUserGroupConsistency();
              } catch (PermissionKeyRequiredException ignored) {
              } catch (InconsistentUserGroupValueException e) {
                JOptionPane.showMessageDialog(
                    getView().getTopComponent(),
                    "Die Transaktionsdaten weichen von den Kontoständen ab.\n"
                        + "Bitte den Vorstand oder die Buchhaltung informieren!",
                    "Inkonsistenter Datenbestand",
                    JOptionPane.WARNING_MESSAGE);
              } catch (MissingFullMemberException f) {
                JOptionPane.showMessageDialog(
                    getView().getTopComponent(),
                    f.getMessage(),
                    "Benutzergruppe ohne Vollmitglied",
                    JOptionPane.WARNING_MESSAGE);
              }
              if (shouldForcePasswordChange(LogInModel.getLoggedIn())) {
                new ChangePasswordController(LogInModel.getLoggedIn(), true)
                    .openIn(new SubWindow(view.traceViewContainer()));
                view.indicateProgress(false);
                view.messageLoginAgain();
                view.clearPassword();
              } else {
                new MenuController().openTab();
                view.back();
              }
            })
        .start();
  }

  private static void loadUserSettings() {
    try {
      UIManager.setLookAndFeel(
          UserSetting.THEME.getEnumValue(Theme.class, LogInModel.getLoggedIn()).getLookAndFeel());
      UiTools.scaleFonts(UserSetting.FONT_SCALE_FACTOR.getFloatValue(LogInModel.getLoggedIn()));
      SwingUtilities.updateComponentTreeUI(
          TabbedPaneModel.getMainPanel().getView().getTabbedPane());
    } catch (UnsupportedLookAndFeelException e) {
      Tools.showUnexpectedErrorWarning(e);
    } catch (PermissionKeyRequiredException p) {
      Main.logger.error(p.getMessage(), p);
      JOptionPane.showMessageDialog(
          null,
          "Keine ausreichenden Zugriffsrechte!",
          "Anmeldung Fehlgeschlagen",
          JOptionPane.ERROR_MESSAGE);

    } catch (Exception u) {
      Tools.showUnexpectedErrorWarning(u);
    }
  }

  @Override
  protected void closed() {
    // shutdowns application if tab gets closed without any other other tab is opened
    // 1 because the model removes the tab from the model after it notified the tab itself
    if (TabbedPaneModel.getMainPanel().getModel().size() == 1) {
      System.exit(0);
    }
  }
}
