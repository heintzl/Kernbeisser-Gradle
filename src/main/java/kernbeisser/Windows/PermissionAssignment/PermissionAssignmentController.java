package kernbeisser.Windows.PermissionAssignment;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import kernbeisser.CustomComponents.ObjectTable.Column;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionConstants;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Security.Access.Access;
import kernbeisser.Security.Access.AccessManager;
import kernbeisser.Security.Key;
import kernbeisser.Security.StaticPermissionChecks;
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

  public static PermissionAssignmentController cashierPermissionController() {
    StaticPermissionChecks.getStaticInstance().checkActionGrantCashierPermission();
    AccessManager before = Access.getDefaultManager();
    Access.setDefaultManager(AccessManager.NO_ACCESS_CHECKING);
    try {
      return new PermissionAssignmentController() {
        @Override
        public void fillView(PermissionAssignmentView permissionAssignmentView) {
          permissionAssignmentView.setPermissions(
              Collections.singletonList(PermissionConstants.CASHIER.getPermission()));
        }
      };
    } finally {
      Access.setDefaultManager(before);
    }
  }
}
