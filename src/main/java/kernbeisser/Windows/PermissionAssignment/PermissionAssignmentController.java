package kernbeisser.Windows.PermissionAssignment;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.*;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Security.Key;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;

public class PermissionAssignmentController
    extends Controller<PermissionAssignmentView, PermissionAssignmentModel> {

  @Linked private final CollectionController<User> user;
  private boolean onlyCashier = false;

  @Key(PermissionKey.ACTION_OPEN_PERMISSION_ASSIGNMENT)
  public PermissionAssignmentController() throws PermissionKeyRequiredException {
    super(new PermissionAssignmentModel());
    user = getUserSource();
  }

  @Key(PermissionKey.ACTION_GRANT_CASHIER_PERMISSION)
  private PermissionAssignmentController(boolean dummy) throws PermissionKeyRequiredException {
    super(new PermissionAssignmentModel());
    this.onlyCashier = true;
    user = getUserSource();
  }

  private CollectionController<User> getUserSource() {
    return new CollectionController<>(
        new ArrayList<>(),
        Source.empty(),
        Columns.create("Vorname", User::getFirstName).withDefaultFilter(),
        Columns.create("Nachname", User::getSurname).withDefaultFilter(),
        Columns.create("Dienste", User::getJobsAsString).withDefaultFilter());
  }

  @Override
  public void fillView(PermissionAssignmentView permissionAssignmentView) {
    permissionAssignmentView.setPermissions(
        model.getPermissions().stream()
            .filter(
                p ->
                    (!p.getName()
                            .matches(
                                "@KEY_PERMISSION|@IN_RELATION_TO_OWN_USER|@IMPORT|@APPLICATION")
                        && (p.getName().equals("@CASHIER") || !onlyCashier)))
            .collect(Collectors.toList()));
    user.getView().addSearchbox(CollectionView.BOTH);
    JButton toggleClipBoardFilter = new JButton("Auf Zwischenablage filtern");
    toggleClipBoardFilter.addActionListener(
        e -> user.getView().addRowFilter(model.getClpBoardRowFilter(), 1));
    user.addControls(toggleClipBoardFilter);
  }

  public void loadPermission(ActionEvent actionEvent) {
    Optional<Permission> permission = getView().getSelectedPermission();
    if (!permission.isPresent()) {
      return;
    }
    applyChanges();
    user.setLoadedAndSource(model.assignedUsers(permission.get()), model::allUsers);
    model.setRecent(permission.get());
  }

  private void applyChanges() {
    Optional<Collection<User>> before = model.getRecent().map(model::assignedUsers);
    if (!before.isPresent() || before.get().equals(user.getModel().getLoaded())) return;
    model.setPermission(
        model.getRecent().get(), user.getModel().getLoaded(), () -> getView().confirmChanges());
  }

  public static PermissionAssignmentController cashierPermissionController() {
    return new PermissionAssignmentController(true);
  }

  @Override
  protected void closed() {
    model.setPermission(model.getRecent().get(), user.getModel().getLoaded(), () -> true);
  }
}
