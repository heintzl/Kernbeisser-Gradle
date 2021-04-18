package kernbeisser.Windows.PermissionAssignment;

import java.util.List;
import java.util.Optional;
import javax.swing.JComponent;
import javax.swing.JPanel;
import kernbeisser.CustomComponents.ComboBox.AdvancedComboBox;
import kernbeisser.DBEntities.Permission;
import kernbeisser.DBEntities.User;
import kernbeisser.Windows.CollectionView.CollectionController;
import kernbeisser.Windows.CollectionView.CollectionView;
import kernbeisser.Windows.MVC.IView;
import kernbeisser.Windows.MVC.Linked;
import org.jetbrains.annotations.NotNull;

public class PermissionAssignmentView implements IView<PermissionAssignmentController> {

  private JPanel main;
  private AdvancedComboBox<Permission> permissions;
  private CollectionView<User> collectionView;

  @Linked private CollectionController<User> user;

  @Override
  public void initialize(PermissionAssignmentController controller) {
    permissions.addActionListener(controller::loadPermission);
  }

  void setPermissions(List<Permission> permissions) {
    this.permissions.setItems(permissions);
  }

  @Override
  public @NotNull JComponent getContent() {
    return main;
  }

  private void createUIComponents() {
    permissions = new AdvancedComboBox<>(Permission::getNeatName);
    collectionView = user.getView();
  }

  Optional<Permission> getSelectedPermission() {
    return permissions.getSelected();
  }

  @Override
  public String getTitle() {
    return "Berechtigung erteilen";
  }
}
