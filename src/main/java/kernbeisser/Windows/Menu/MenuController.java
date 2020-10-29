package kernbeisser.Windows.Menu;

import javax.swing.*;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import org.jetbrains.annotations.NotNull;

public class MenuController extends Controller<MenuView, MenuModel> {

  public MenuController() {
    super(new MenuModel());
  }

  @NotNull
  @Override
  public MenuModel getModel() {
    return model;
  }

  @Override
  public void fillView(MenuView menuView) {}

  @Override
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[0];
  }

  private boolean alreadyAsked = false;

  @Override
  public boolean commitClose() {
    if (alreadyAsked) return true;
    if (JOptionPane.showConfirmDialog(
            getView().getTopComponent(),
            "Sind sie Sicher das sie sich Ausloggen und\ndamit alle geöfnteten Tabs / Fenster schließen wollen")
        == 0) {
      SwingUtilities.updateComponentTreeUI(TabbedPaneModel.MAIN_PANEL.getView().getTopComponent());
      getView().traceViewContainer().getLoaded().withCloseEvent(SimpleLogInController::new);
      alreadyAsked = true;
      SwingUtilities.invokeLater(
          () -> {
            TabbedPaneModel.resetMainPanel();
            new SimpleLogInController().openTab();
          });
      return true;
    }
    return false;
  }

  @Override
  protected void closed() {}
}
