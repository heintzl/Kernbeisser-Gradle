package kernbeisser.Windows.Menu;

import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Security.Key;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.PreOrder.PreOrderController;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import kernbeisser.Windows.Trasaction.TransactionController;
import kernbeisser.Windows.UserInfo.UserInfoController;
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
  public void fillView(MenuView menuView) {
    // Access.setDefaultManager(new AccessAnalyser());
  }

  @Key(PermissionKey.ACTION_OPEN_OWN_PRE_ORDER)
  public PreOrderController getOwnPreorderController() {
    return new PreOrderController(true);
  }

  @Key(PermissionKey.ACTION_OPEN_PRE_ORDER)
  public PreOrderController getPreorderController() {
    return new PreOrderController(false);
  }

  @Key(PermissionKey.ACTION_EDIT_OWN_DATA)
  public UserInfoController getOwnUserInfoController() {
    return new UserInfoController(LogInModel.getLoggedInFromDB());
  }

  @Key(PermissionKey.ACTION_TRANSACTION_FROM_KB)
  public TransactionController getPayInTransactionController() {
    return new TransactionController(LogInModel.getLoggedInFromDB(), TransactionType.PAYIN);
  }

  @Key(PermissionKey.ACTION_OPEN_DB_LOG_IN)
  public DBLogInController getDBLoginController() {
    return new DBLogInController();
  }

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
      alreadyAsked = true;
      if (TabbedPaneModel.resetMainPanel()) {
        new SimpleLogInController().openTab();
        return true;
      }
      alreadyAsked = false;
      return false;
    }
    return false;
  }

  @Override
  protected void closed() {
    SwingUtilities.getWindowAncestor(getView().getContent()).dispose();
  }

  @Key(PermissionKey.ACTION_ADD_BEGINNER)
  public FormEditorController<User> generateAddBeginnerForm() {
    User beginnerUser = new User();
    Access.putException(beginnerUser, AccessManager.NO_ACCESS_CHECKING);
    return FormEditorController.open(
        beginnerUser, UserController.getBeginnerUserController(), Mode.ADD);
  }
}
