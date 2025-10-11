package kernbeisser.Windows.Menu;

import javax.swing.*;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.Mode;
import kernbeisser.Enums.PreOrderCreator;
import kernbeisser.Enums.Setting;
import kernbeisser.Enums.TransactionType;
import kernbeisser.Forms.FormEditor.FormEditorController;
import kernbeisser.Forms.FormImplemetations.User.UserController;
import kernbeisser.Security.Access.UserRelatedAccessManager;
import kernbeisser.StartUp.LogIn.DBLogInController;
import kernbeisser.Useful.UiTools;
import kernbeisser.Windows.DatabaseView.DatabaseViewController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.LogIn.SimpleLogIn.SimpleLogInController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.PreOrder.PreOrderController;
import kernbeisser.Windows.TabbedPane.TabbedPaneModel;
import kernbeisser.Windows.Transaction.TransactionController;
import kernbeisser.Windows.UserInfo.UserInfoController;
import org.jetbrains.annotations.NotNull;
import rs.groump.Access;
import rs.groump.AccessManager;
import rs.groump.Key;
import rs.groump.PermissionKey;

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
    return new PreOrderController(PreOrderCreator.SELF, LogInModel.getLoggedIn());
  }

  @Key(PermissionKey.ACTION_OPEN_PRE_ORDER)
  public PreOrderController getPreorderController() {
    return new PreOrderController(PreOrderCreator.PRE_ORDER_MANAGER, null);
  }

  @Key(PermissionKey.ACTION_EDIT_OWN_DATA)
  public UserInfoController getOwnUserInfoController() {
    return new UserInfoController(LogInModel.getLoggedIn());
  }

  @Key(PermissionKey.ACTION_TRANSACTION_FROM_KB)
  public TransactionController getPayInTransactionController() {
    return new TransactionController(LogInModel.getLoggedIn(), TransactionType.PAYIN);
  }

  @Key(PermissionKey.ACTION_TRANSACTION_FROM_OTHER)
  public TransactionController getInternalTransactionController() {
    return new TransactionController(LogInModel.getLoggedIn(), TransactionType.USER_GENERATED);
  }

  @Key(PermissionKey.ACTION_OPEN_DB_LOG_IN)
  public DBLogInController getDBLoginController() {
    return new DBLogInController();
  }

  @Key(PermissionKey.ACTION_OPEN_DB_LOG_IN)
  public DatabaseViewController createDatabaseViewController() {
    return new DatabaseViewController();
  }

  private boolean alreadyAsked = false;

  @Override
  public boolean commitClose() {
    MenuView view = getView();
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
        UiTools.scaleFonts(Setting.LABEL_SCALE_FACTOR.getFloatValue());
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

  @Key(PermissionKey.ACTION_ADD_TRIAL_MEMBER)
  public FormEditorController<User> generateAddBeginnerForm() {
    User beginnerUser = new User();
    ((UserRelatedAccessManager) Access.getAccessManager())
        .registerException(beginnerUser, AccessManager.ACCESS_GRANTED);
    return FormEditorController.create(
        beginnerUser, UserController.getBeginnerUserController(), Mode.ADD);
  }
}
