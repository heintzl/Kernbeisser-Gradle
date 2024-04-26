package kernbeisser.Windows.PermissionGranterAssignment;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import kernbeisser.CustomComponents.ObjectTable.Columns.Columns;
import kernbeisser.DBEntities.Permission;
import kernbeisser.Forms.ObjectForm.Components.Source;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.Controller;
import kernbeisser.Windows.MVC.Linked;
import rs.groump.Key;
import rs.groump.PermissionKey;

public class PermissionGranterAssignmentController
    extends Controller<PermissionGranterAssignmentView, PermissionGranterAssignmentModel> {

  @Linked private final CollectionController<Permission> permission;

  @Key(PermissionKey.ACTION_OPEN_PERMISSION_GRANT_ASSIGNMENT)
  public PermissionGranterAssignmentController() {
    super(new PermissionGranterAssignmentModel());
    permission = getPermissionSource();
  }

  private CollectionController<Permission> getPermissionSource() {
    return new CollectionController<>(
        new ArrayList<>(),
        Source.empty(),
        Columns.create("Rolle", Permission::getNeatName).withDefaultFilter());
  }

  @Override
  public void fillView(PermissionGranterAssignmentView permissionGranterAssignmentView) {
    permissionGranterAssignmentView.setPermissions(
        model.allPermissions().stream()
            .filter(
                p ->
                    (!p.getName()
                        .matches("@KEY_PERMISSION|@IN_RELATION_TO_OWN_USER|@IMPORT|@APPLICATION")))
            .collect(Collectors.toList()));
    permission.getView().addSearchbox(CollectionView.BOTH);
  }

  public void loadPermission(ActionEvent actionEvent) {
    Optional<Permission> permission = getView().getSelectedPermission();
    if (!permission.isPresent()) {
      return;
    }
    applyChanges();
    this.permission.setLoadedAndSource(permission.get().getGranters(), model::allPermissions);
    model.setRecent(permission.get());
  }

  private void applyChanges() {
    Optional<Collection<Permission>> before = model.getRecent().map(Permission::getGranters);
    if (!before.isPresent() || before.get().equals(permission.getModel().getLoaded())) return;
    model.setPermission(
        model.getRecent().get(),
        permission.getModel().getLoaded(),
        () -> getView().confirmChanges());
  }

  @Override
  protected void closed() {
    model.setPermission(model.getRecent().get(), permission.getModel().getLoaded(), () -> true);
  }
}
