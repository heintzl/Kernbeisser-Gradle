package kernbeisser.Windows.AdminTools;

import java.awt.event.ActionEvent;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.Key;
import kernbeisser.Windows.EditUserGroup.EditUserGroupController;
import kernbeisser.Windows.LogIn.LogInModel;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainers.SubWindow;

public class AdminToolController extends Controller<AdminToolView, AdminToolModel> {

  @Key(PermissionKey.ACTION_OPEN_ADMIN_TOOLS)
  public AdminToolController() throws PermissionKeyRequiredException {
    super(new AdminToolModel());
  }

  @Override
  public void fillView(AdminToolView adminToolView) {
    adminToolView.setUsers(model.getAllUsers());
  }

  public void restedPassword(ActionEvent actionEvent) {
    User user = getView().getSelectedUser();
    if (getView().verifyPasswordChange(user.getUsername())) {
      getView().showPasswordToken(model.resetPassword(user));
    }
  }

  public void openUserGroupEditor(ActionEvent actionEvent) {
    new EditUserGroupController(getView().getSelectedUser(), LogInModel.getLoggedIn())
        .withCloseEvent(() -> fillView(getView()))
        .openIn(new SubWindow(getView().traceViewContainer()));
  }
}
