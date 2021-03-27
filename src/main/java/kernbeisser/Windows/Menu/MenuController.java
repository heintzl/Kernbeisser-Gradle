package kernbeisser.Windows.Menu;

import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import lombok.var;
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

  private boolean alreadyAsked = false;

  @Override
  public boolean commitClose() {
    var view = getView();
    if (alreadyAsked) return true;
    if (JOptionPane.showConfirmDialog(
            view.getTopComponent(),
            "Bist du sicher, dass du dich abmelden und\n"
                + "damit alle geöffneten Tabs / Fenster schließen möchtest?",
            "Abmelden",
            JOptionPane.YES_NO_OPTION)
        == 0) {
      SwingUtilities.updateComponentTreeUI(TabbedPaneModel.MAIN_PANEL.getView().getTopComponent());
      view.traceViewContainer().getLoaded().withCloseEvent(SimpleLogInController::new);
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
  protected void closed() {
    SwingUtilities.getWindowAncestor(getView().getContent()).dispose();
  }

  public FormEditorController<User> generateAddBeginnerForm() {
    return FormEditorController.open(new User(), new UserController(), Mode.ADD);
  }
}
