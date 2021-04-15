package kernbeisser.Windows.PermissionAssignment;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Security.Key;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;

public class PermissionAssignmentController
    extends Controller<PermissionAssignmentView, PermissionAssignmentModel> {

  @Linked private final CollectionController<User> user;

  @Key(PermissionKey.ACTION_OPEN_PERMISSION_ASSIGNMENT)
  public PermissionAssignmentController() throws PermissionKeyRequiredException {
    super(new PermissionAssignmentModel());
    user =
        new CollectionController<>(
            new ArrayList<>(), Source.empty(), Column.create("Benutzername", User::getUsername));
  }

  @Override
  public void fillView(PermissionAssignmentView permissionAssignmentView) {
    permissionAssignmentView.setPermissions(model.getPermissions());
  }

  public void loadPermission(ActionEvent actionEvent) {
    Optional<Permission> permission = getView().getSelectedPermission();
    if (!permission.isPresent()) {
      return;
    }
    applyChanges();
    user.setLoadedAndSource(model.assignedUsers(permission.get()), getModel().allUsers());
    model.setRecent(permission.get());
  }

  private void applyChanges() {
    Optional<Collection<User>> before = model.getRecent().map(model::assignedUsers);
    if (!before.isPresent() || before.get().equals(user.getModel().getLoaded())) return;
    model.setPermission(model.getRecent().get(), user.getModel().getLoaded());
  }
}
