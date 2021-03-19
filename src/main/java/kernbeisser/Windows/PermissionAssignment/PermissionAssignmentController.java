package kernbeisser.Windows.PermissionAssignment;

import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.MVC.Controller;

public class PermissionAssignmentController
    extends Controller<PermissionAssignmentView, PermissionAssignmentModel> {

  public PermissionAssignmentController() throws PermissionKeyRequiredException {
    super(new PermissionAssignmentModel());
  }

  @Override
  public void fillView(PermissionAssignmentView permissionAssignmentView) {}
}
