package kernbeisser.Windows.LogIn.SimpleLogIn;

import kernbeisser.Enums.Key;
import kernbeisser.Enums.Theme;
import kernbeisser.Enums.UserSetting;
import kernbeisser.Exeptions.AccessDeniedException;
import kernbeisser.Exeptions.PermissionRequired;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.TabbedPanel.DefaultTab;
import kernbeisser.Windows.TabbedPanel.TabbedPaneModel;
import kernbeisser.Windows.View;
import kernbeisser.Windows.WindowImpl.JFrameWindow;
import kernbeisser.Windows.UserMenu.UserMenuController;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

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
    public Key[] getRequiredKeys() {
        return new Key[0];
    }

    public void logIn() {
        try {
            model.logIn(view.getUsername(),view.getPassword());
            removeSelf();
            loadUserSettings();
            new UserMenuController().openTab("Menu");
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
            e.printStackTrace();
        }
    }
}
