package kernbeisser.Windows.Menu;

import kernbeisser.Enums.PermissionKey;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.TabbedPanel.TabbedPaneModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MenuController implements Controller<MenuView,MenuModel> {

    private final MenuModel model;
    private final MenuView view;

    public MenuController() {
        model = new MenuModel();
        view = new MenuView();
    }


    @NotNull
    @Override
    public MenuView getView() {
        return view;
    }

    @NotNull
    @Override
    public MenuModel getModel() {
        return model;
    }

    @Override
    public void fillUI() {
    }

    @Override
    public PermissionKey[] getRequiredKeys() {
        return new PermissionKey[0];
    }

    @Override
    public boolean commitClose() {
        if (JOptionPane.showConfirmDialog(getView().getTopComponent(), "Sind sie Sicher das sie sich Ausloggen und\ndamit alle geöfnteten Tabs / Fenster schließen wollen") == 0) {
            TabbedPaneModel.DEFAULT_TABBED_PANE.unsafeClose(asTab("Menu"));
            if(TabbedPaneModel.DEFAULT_TABBED_PANE.clear()){
                try {
                    Main.setSettingLAF();
                } catch (UnsupportedLookAndFeelException e) {
                    Tools.showUnexpectedErrorWarning(e);
                }
                SwingUtilities.updateComponentTreeUI(TabbedPaneModel.DEFAULT_TABBED_PANE.getView().getTopComponent());
                new SimpleLogInController().openTab("Log In");
            }else {
                new MenuController().openTab("Menu");
            }
        }
        return false;
    }
}
