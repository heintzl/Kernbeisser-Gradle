package kernbeisser.Windows.PermissionAssignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Windows.MVC.Controller;

public class PermissionAssignmentController
    extends Controller<PermissionAssignmentView, PermissionAssignmentModel>
    implements ActionListener {

  public PermissionAssignmentController() throws PermissionKeyRequiredException {
    super(new PermissionAssignmentModel());
  }

  @Override
  public void fillView(PermissionAssignmentView permissionAssignmentView) {
    permissionAssignmentView.setPermissions(model.getPermissions());
  }

  @Override
  public void actionPerformed(ActionEvent e) {}
}
