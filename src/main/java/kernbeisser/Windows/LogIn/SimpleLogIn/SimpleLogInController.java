package kernbeisser.Windows.LogIn.SimpleLogIn;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.ChangePassword.ChangePasswordController;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.Menu.MenuController;
import kernbeisser.Windows.TabbedPanel.TabbedPaneModel;
import kernbeisser.Windows.View;
import kernbeisser.Windows.WindowImpl.SubWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SimpleLogInController implements Controller<SimpleLogInView,SimpleLogInModel> {

    private SimpleLogInView view;
    private SimpleLogInModel model;

    public SimpleLogInController(){
        this.model = new SimpleLogInModel();
        this.view = new SimpleLogInView(this);
    }



    @Override
    public @NotNull SimpleLogInView getView() {
        return view;
    }


    @Override
    public @NotNull SimpleLogInModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {

    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    public void logIn() {
        try {
            model.logIn(view.getUsername(),view.getPassword());
            loadUserSettings();
            if (LogInModel.getLoggedIn().getLastPasswordChange().until(Instant.now(), ChronoUnit.DAYS) > Setting.FORCE_PASSWORD_CHANGE_AFTER.getIntValue()) {
                new ChangePasswordController(LogInModel.getLoggedIn(),true).openAsWindow(getView().getWindow(),
                                                                                         SubWindow::new);
            }else {
                removeSelf();
                new MenuController().openTab("Menu");
            }
        } catch (AccessDeniedException e) {
            view.accessDenied();
        } catch (PermissionRequired permissionRequired) {
            view.permissionRequired();
        }
    }


    private void loadUserSettings(){
        try {
            UIManager.setLookAndFeel(UserSetting.THEME.getEnumValue(Theme.class, LogInModel.getLoggedIn()).getLookAndFeel());
            SwingUtilities.updateComponentTreeUI(TabbedPaneModel.DEFAULT_TABBED_PANE.getView().getTopComponent());
        } catch (UnsupportedLookAndFeelException e) {
            Tools.showUnexpectedErrorWarning(e);
        }
    }
}
