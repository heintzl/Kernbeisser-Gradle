package kernbeisser.Windows.AdminTools;

import java.awt.event.ActionEvent;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Security.StaticMethodTransformer.StaticAccessPoint;
import kernbeisser.Windows.EditUserGroup.EditUserGroupController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.ViewContainers.SubWindow;

public class AdminToolController extends Controller<AdminToolView, AdminToolModel> {

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
    new EditUserGroupController(getView().getSelectedUser())
        .withCloseEvent(() -> fillView(getView()))
        .openIn(new SubWindow(getView().traceViewContainer()));
  }

  @Override
  @StaticAccessPoint
  public PermissionKey[] getRequiredKeys() {
    return new PermissionKey[] {PermissionKey.ACTION_OPEN_ADMIN_TOOLS};
  }
}
